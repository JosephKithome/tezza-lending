package com.tezza.lending.loan.serviceimpl;

import com.tezza.lending.customer.model.Customer;
import com.tezza.lending.customer.repository.CustomerRepository;
import com.tezza.lending.exception.BusinessRuleException;
import com.tezza.lending.exception.ResourceNotFoundException;
import com.tezza.lending.loan.model.Loan;
import com.tezza.lending.loan.model.LoanInstallment;
import com.tezza.lending.loan.model.LoanRequest;
import com.tezza.lending.loan.model.LoanResponse;
import com.tezza.lending.loan.model.LoanStatus;
import com.tezza.lending.loan.model.LoanStructure;
import com.tezza.lending.loan.model.Repayment;
import com.tezza.lending.loan.model.RepaymentRequest;
import com.tezza.lending.loan.model.RepaymentResponse;
import com.tezza.lending.loan.repository.LoanRepository;
import com.tezza.lending.loan.repository.RepaymentRepository;
import com.tezza.lending.loan.service.LoanService;
import com.tezza.lending.notification.model.NotificationEventType;
import com.tezza.lending.notification.service.NotificationService;
import com.tezza.lending.product.model.FeeApplicationStage;
import com.tezza.lending.product.model.FeeCalculationType;
import com.tezza.lending.product.model.FeeType;
import com.tezza.lending.product.model.LoanProduct;
import com.tezza.lending.product.repository.LoanProductRepository;
import com.tezza.lending.product.model.ProductFee;
import com.tezza.lending.product.model.TenureUnit;
import com.tezza.lending.shared.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final LoanRepository loanRepository;
    private final RepaymentRepository repaymentRepository;
    private final CustomerRepository customerRepository;
    private final LoanProductRepository productRepository;
    private final NotificationService notificationService;

    public LoanServiceImpl(LoanRepository loanRepository,
                           RepaymentRepository repaymentRepository,
                           CustomerRepository customerRepository,
                           LoanProductRepository productRepository,
                           NotificationService notificationService) {
        this.loanRepository = loanRepository;
        this.repaymentRepository = repaymentRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public LoanResponse disburse(LoanRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        LoanProduct product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        LoanStructure structure = request.structure() == null ? LoanStructure.LUMP_SUM : request.structure();
        validateDisbursement(request, structure, customer, product);

        BigDecimal originationFees = fees(product, request.principalAmount(), FeeApplicationStage.ORIGINATION);
        BigDecimal openingBalance = request.principalAmount().add(originationFees).setScale(2, RoundingMode.HALF_UP);

        Loan loan = new Loan();
        loan.setLoanNumber(generateLoanNumber());
        loan.setCustomer(customer);
        loan.setProduct(product);
        loan.setStructure(structure);
        loan.setStatus(LoanStatus.OPEN);
        loan.setPrincipalAmount(request.principalAmount());
        loan.setOutstandingAmount(openingBalance);
        loan.setTotalFeesApplied(originationFees);
        loan.setOriginationDate(LocalDate.now());
        loan.setDueDate(resolveDueDate(LocalDate.now(), product));
        loan.setConsolidatedBilling(request.consolidatedBilling());
        loan.setConsolidatedDueDate(request.consolidatedBilling() && request.consolidatedDueDate() == null
                ? loan.getDueDate()
                : request.consolidatedDueDate());
        loan.setLateFeeApplied(false);
        buildInstallments(loan, request.installmentCount());

        Loan saved = loanRepository.save(loan);
        notificationService.publish(NotificationEventType.LOAN_DISBURSED, customer, saved);
        return LoanResponse.from(saved);
    }

    @Override
    public RepaymentResponse repay(Long loanId, RepaymentRequest request) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        if (isRepaymentBlocked(loan.getStatus())) {
            throw new BusinessRuleException("Repayment is not allowed for loan status " + loan.getStatus());
        }

        BigDecimal previousBalance = loan.getOutstandingAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal receivedAmount = request.amount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal appliedAmount = receivedAmount.min(previousBalance).setScale(2, RoundingMode.HALF_UP);
        BigDecimal overpaymentAmount = receivedAmount.subtract(appliedAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal balance = previousBalance.subtract(appliedAmount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        loan.setOutstandingAmount(balance);
        boolean fullyPaid = balance.compareTo(BigDecimal.ZERO) == 0;
        if (fullyPaid) {
            loan.setStatus(LoanStatus.CLOSED);
            loan.getInstallments().forEach(installment -> {
                installment.setAmountPaid(installment.getAmountDue());
                installment.setPaid(true);
            });
        }

        Repayment repayment = new Repayment();
        repayment.setLoan(loan);
        repayment.setAmount(receivedAmount);
        repayment.setAppliedAmount(appliedAmount);
        repayment.setOverpaymentAmount(overpaymentAmount);
        repayment.setChannel(isBlank(request.channel()) ? "SYSTEM" : request.channel());
        repayment.setExternalReference(isBlank(request.externalReference())
                ? generateRepaymentReference()
                : request.externalReference());
        repayment.setPaidAt(java.time.Instant.now());

        Repayment saved = repaymentRepository.save(repayment);
        loanRepository.save(loan);

        Map<String, String> repaymentVariables = repaymentVariables(
                saved,
                previousBalance,
                balance,
                totalReceived(loanId),
                totalApplied(loanId));

        notificationService.publishEmail(
                NotificationEventType.REPAYMENT_ACKNOWLEDGEMENT,
                loan.getCustomer(),
                loan,
                repaymentVariables);
        if (fullyPaid) {
            notificationService.publishEmail(
                    NotificationEventType.LOAN_FULLY_REPAID,
                    loan.getCustomer(),
                    loan,
                    repaymentVariables);
        }

        return RepaymentResponse.from(saved);
    }

    @Override
    public LoanResponse cancel(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        if (loan.getStatus() == LoanStatus.CLOSED || loan.getStatus() == LoanStatus.WRITTEN_OFF) {
            throw new BusinessRuleException("Closed or written-off loans cannot be cancelled");
        }
        loan.setStatus(LoanStatus.CANCELLED);
        return LoanResponse.from(loanRepository.save(loan));
    }

    @Override
    public LoanResponse writeOff(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        if (loan.getStatus() == LoanStatus.CLOSED) {
            throw new BusinessRuleException("Closed loans cannot be written off");
        }
        loan.setStatus(LoanStatus.WRITTEN_OFF);
        return LoanResponse.from(loanRepository.save(loan));
    }

    @Override
    public List<LoanResponse> sweepOverdue(LocalDate businessDate) {
        List<Loan> dueLoans = loanRepository.findByStatusInAndDueDateBefore(
                List.of(LoanStatus.OPEN, LoanStatus.OVERDUE),
                businessDate);
        for (Loan loan : dueLoans) {
            loan.setStatus(LoanStatus.OVERDUE);
            applyDailyAccrualIfNeeded(loan, businessDate);
            int triggerDays = loan.getProduct().getDaysAfterDueForFeeApplication();
            if (!loan.isLateFeeApplied() && !businessDate.isBefore(loan.getDueDate().plusDays(triggerDays))) {
                BigDecimal lateFees = fees(loan.getProduct(), loan.getOutstandingAmount(), FeeApplicationStage.AFTER_DUE_DATE);
                if (lateFees.compareTo(BigDecimal.ZERO) > 0) {
                    loan.setOutstandingAmount(loan.getOutstandingAmount().add(lateFees).setScale(2, RoundingMode.HALF_UP));
                    loan.setTotalFeesApplied(loan.getTotalFeesApplied().add(lateFees).setScale(2, RoundingMode.HALF_UP));
                    loan.setLateFeeApplied(true);
                }
            }
            notificationService.publish(NotificationEventType.OVERDUE_NOTICE, loan.getCustomer(), loan);
        }
        return loanRepository.saveAll(dueLoans).stream().map(LoanResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LoanResponse get(Long id) {
        return loanRepository.findById(id)
                .map(LoanResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse list(Pageable pageable) {
        return PagedResponse.from(loanRepository.findAll(pageable).map(LoanResponse::from));
    }

    @Override
    public void delete(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        if (repaymentRepository.countByLoanId(id) > 0) {
            throw new BusinessRuleException("Loan cannot be deleted because repayments are linked to it");
        }
        loanRepository.delete(loan);
    }

    private void validateDisbursement(LoanRequest request, LoanStructure structure, Customer customer, LoanProduct product) {
        if (!product.isActive()) {
            throw new BusinessRuleException("Product is inactive");
        }
        if (request.principalAmount().compareTo(product.getMinimumAmount()) < 0
                || request.principalAmount().compareTo(product.getMaximumAmount()) > 0) {
            throw new BusinessRuleException("Loan amount is outside product amount range");
        }
        if (request.principalAmount().compareTo(customer.getLoanLimit()) > 0) {
            throw new BusinessRuleException("Loan amount exceeds customer loan limit");
        }
        if (structure == LoanStructure.INSTALLMENT
                && (request.installmentCount() == null || request.installmentCount() < 2)) {
            throw new BusinessRuleException("Installment loans require installmentCount of at least 2");
        }
    }

    private void buildInstallments(Loan loan, Integer installmentCount) {
        int count = loan.getStructure() == LoanStructure.INSTALLMENT ? installmentCount : 1;
        BigDecimal installmentAmount = loan.getOutstandingAmount()
                .divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

        for (int index = 1; index <= count; index++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setInstallmentNumber(index);
            installment.setAmountDue(installmentAmount);
            installment.setAmountPaid(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            installment.setPaid(false);
            installment.setDueDate(installmentDueDate(loan, count, index));
            loan.addInstallment(installment);
        }
    }

    private boolean isRepaymentBlocked(LoanStatus status) {
        return status == LoanStatus.CLOSED
                || status == LoanStatus.CANCELLED
                || status == LoanStatus.WRITTEN_OFF;
    }

    private LocalDate installmentDueDate(Loan loan, int count, int index) {
        if (count == 1) {
            return loan.getDueDate();
        }

        long interval = (long) index * daysBetween(loan.getOriginationDate(), loan.getDueDate()) / count;
        return loan.getOriginationDate().plusDays(interval);
    }

    private long daysBetween(LocalDate start, LocalDate end) {
        return Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(start, end));
    }

    private LocalDate resolveDueDate(LocalDate startDate, LoanProduct product) {
        if (product.getTenureUnit() == TenureUnit.MONTHS) {
            return startDate.plusMonths(product.getTenureValue());
        }
        return startDate.plusDays(product.getTenureValue());
    }

    private BigDecimal fees(LoanProduct product, BigDecimal basis, FeeApplicationStage stage) {
        return product.getFees().stream()
                .filter(fee -> fee.getApplicationStage() == stage)
                .filter(fee -> stage != FeeApplicationStage.ORIGINATION || fee.getFeeType() == FeeType.SERVICE_FEE)
                .filter(fee -> stage != FeeApplicationStage.DAILY_ACCRUAL || fee.getFeeType() == FeeType.DAILY_FEE)
                .filter(fee -> stage != FeeApplicationStage.AFTER_DUE_DATE || fee.getFeeType() == FeeType.LATE_FEE)
                .map(fee -> fee.getCalculationType() == FeeCalculationType.PERCENTAGE
                        ? basis.multiply(fee.getValue()).divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP)
                        : fee.getValue())
                .reduce(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void applyDailyAccrualIfNeeded(Loan loan, LocalDate businessDate) {
        if (loan.getLastFeeAccrualDate() != null && !loan.getLastFeeAccrualDate().isBefore(businessDate)) {
            return;
        }
        BigDecimal dailyFees = fees(loan.getProduct(), loan.getOutstandingAmount(), FeeApplicationStage.DAILY_ACCRUAL);
        if (dailyFees.compareTo(BigDecimal.ZERO) > 0) {
            loan.setOutstandingAmount(loan.getOutstandingAmount().add(dailyFees).setScale(2, RoundingMode.HALF_UP));
            loan.setTotalFeesApplied(loan.getTotalFeesApplied().add(dailyFees).setScale(2, RoundingMode.HALF_UP));
            loan.setLastFeeAccrualDate(businessDate);
        }
    }

    private String generateLoanNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String candidate;
        do {
            candidate = "LN-" + date + "-" + ThreadLocalRandom.current().nextInt(1000, 9999);
        } while (loanRepository.findByLoanNumber(candidate).isPresent());
        return candidate;
    }

    private String generateRepaymentReference() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        int suffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "RPY-" + date + "-" + suffix;
    }

    private Map<String, String> repaymentVariables(Repayment repayment,
                                                   BigDecimal previousBalance,
                                                   BigDecimal balance,
                                                   BigDecimal totalReceived,
                                                   BigDecimal totalApplied) {
        return Map.ofEntries(
                Map.entry("repaymentAmount", repayment.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString()),
                Map.entry("appliedAmount", repayment.getAppliedAmount().setScale(2, RoundingMode.HALF_UP).toPlainString()),
                Map.entry("overpaymentAmount", repayment.getOverpaymentAmount().setScale(2, RoundingMode.HALF_UP).toPlainString()),
                Map.entry("previousBalance", previousBalance.setScale(2, RoundingMode.HALF_UP).toPlainString()),
                Map.entry("remainingBalance", balance.setScale(2, RoundingMode.HALF_UP).toPlainString()),
                Map.entry("totalPaid", totalReceived.setScale(2, RoundingMode.HALF_UP).toPlainString()),
                Map.entry("totalReceived", totalReceived.setScale(2, RoundingMode.HALF_UP).toPlainString()),
                Map.entry("totalApplied", totalApplied.setScale(2, RoundingMode.HALF_UP).toPlainString()),
                Map.entry("repaymentChannel", repayment.getChannel()),
                Map.entry("repaymentReference", repayment.getExternalReference()),
                Map.entry("paidAt", repayment.getPaidAt().toString()));
    }

    private BigDecimal totalReceived(Long loanId) {
        return repaymentRepository.findByLoanId(loanId).stream()
                .map(Repayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal totalApplied(Long loanId) {
        return repaymentRepository.findByLoanId(loanId).stream()
                .map(repayment -> repayment.getAppliedAmount() == null ? repayment.getAmount() : repayment.getAppliedAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
