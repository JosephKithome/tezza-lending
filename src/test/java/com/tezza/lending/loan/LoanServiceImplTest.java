package com.tezza.lending.loan;


import com.tezza.lending.customer.model.Customer;
import com.tezza.lending.customer.repository.CustomerRepository;
import com.tezza.lending.loan.model.Loan;
import com.tezza.lending.loan.model.LoanRequest;
import com.tezza.lending.loan.model.LoanResponse;
import com.tezza.lending.loan.model.LoanStatus;
import com.tezza.lending.loan.model.LoanStructure;
import com.tezza.lending.loan.model.Repayment;
import com.tezza.lending.loan.model.RepaymentRequest;
import com.tezza.lending.loan.model.RepaymentResponse;
import com.tezza.lending.loan.repository.LoanRepository;
import com.tezza.lending.loan.repository.RepaymentRepository;
import com.tezza.lending.loan.serviceimpl.LoanServiceImpl;
import com.tezza.lending.notification.model.NotificationEventType;
import com.tezza.lending.notification.service.NotificationService;
import com.tezza.lending.product.model.FeeApplicationStage;
import com.tezza.lending.product.model.FeeCalculationType;
import com.tezza.lending.product.model.FeeType;
import com.tezza.lending.product.model.LoanProduct;
import com.tezza.lending.product.repository.LoanProductRepository;
import com.tezza.lending.product.model.ProductFee;
import com.tezza.lending.product.model.TenureUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoanServiceImplTest {
    private final LoanRepository loanRepository = mock(LoanRepository.class);
    private final RepaymentRepository repaymentRepository = mock(RepaymentRepository.class);
    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final LoanProductRepository productRepository = mock(LoanProductRepository.class);
    private final NotificationService notificationService = mock(NotificationService.class);
    private final LoanServiceImpl service = new LoanServiceImpl(
            loanRepository,
            repaymentRepository,
            customerRepository,
            productRepository,
            notificationService);

    @Test
    void disburseCreatesInstallmentsAndAppliesOriginationFee() {
        Customer customer = customer();
        LoanProduct product = product();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanResponse response = service.disburse(new LoanRequest(
                1L,
                1L,
                new BigDecimal("10000.00"),
                LoanStructure.INSTALLMENT,
                4,
                false,
                null));

        assertThat(response.status()).isEqualTo(LoanStatus.OPEN);
        assertThat(response.outstandingAmount()).isEqualByComparingTo("10500.00");
        assertThat(response.totalFeesApplied()).isEqualByComparingTo("500.00");
        assertThat(response.installments()).hasSize(4);
        assertThat(response.installments().get(0).amountDue()).isEqualByComparingTo("2625.00");
        verify(notificationService).publish(any(), any(), any());
    }

    @Test
    void consolidatedBillingUsesExistingCustomerDueDateWhenRequestDoesNotProvideOne() {
        Customer customer = customer();
        LoanProduct product = product();
        LocalDate sharedDueDate = LocalDate.now().plusDays(12);
        Loan existingLoan = new Loan();
        existingLoan.setConsolidatedDueDate(sharedDueDate);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(loanRepository.findFirstByCustomerIdAndConsolidatedBillingTrueAndStatusInOrderByConsolidatedDueDateDesc(
                isNull(),
                any())).thenReturn(Optional.of(existingLoan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanResponse response = service.disburse(new LoanRequest(
                1L,
                1L,
                new BigDecimal("10000.00"),
                LoanStructure.LUMP_SUM,
                null,
                true,
                null));

        assertThat(response.consolidatedBilling()).isTrue();
        assertThat(response.dueDate()).isEqualTo(sharedDueDate);
        assertThat(response.consolidatedDueDate()).isEqualTo(sharedDueDate);
    }

    @Test
    void dueDateReminderSweepPublishesReminderAndMarksLoan() {
        Customer customer = customer();
        Loan loan = loan(customer, product(), new BigDecimal("1000.00"));
        loan.setDueDate(LocalDate.now().plusDays(2));

        when(loanRepository.findByStatusAndDueDateBetweenAndDueDateReminderSentFalse(
                eq(LoanStatus.OPEN),
                any(),
                any())).thenReturn(List.of(loan));
        when(loanRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<LoanResponse> responses = service.sendDueDateReminders(LocalDate.now(), 3);

        assertThat(responses).hasSize(1);
        assertThat(loan.isDueDateReminderSent()).isTrue();
        verify(notificationService).publish(eq(NotificationEventType.DUE_DATE_REMINDER), same(customer), same(loan));
    }

    @Test
    void overdueSweepAppliesLateFeeOnlyAfterFeeTriggerDay() {
        LoanProduct product = product();
        ProductFee lateFee = new ProductFee();
        lateFee.setFeeType(FeeType.LATE_FEE);
        lateFee.setCalculationType(FeeCalculationType.FIXED);
        lateFee.setApplicationStage(FeeApplicationStage.AFTER_DUE_DATE);
        lateFee.setValue(new BigDecimal("250.00"));
        lateFee.setTriggerDaysAfterDue(3);
        product.addFee(lateFee);

        Loan loan = loan(customer(), product, new BigDecimal("1000.00"));
        LocalDate dueDate = LocalDate.now().minusDays(3);
        loan.setDueDate(dueDate);
        when(loanRepository.findByStatusInAndDueDateBefore(any(), any())).thenReturn(List.of(loan));
        when(loanRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.sweepOverdue(dueDate.plusDays(2));

        assertThat(loan.getOutstandingAmount()).isEqualByComparingTo("1000.00");
        assertThat(loan.isLateFeeApplied()).isFalse();

        service.sweepOverdue(dueDate.plusDays(3));

        assertThat(loan.getOutstandingAmount()).isEqualByComparingTo("1250.00");
        assertThat(loan.isLateFeeApplied()).isTrue();
    }

    @Test
    void repaymentClosesLoanWhenBalanceIsFullyPaid() {
        Loan loan = new Loan();
        loan.setLoanNumber("LN-TEST");
        loan.setCustomer(customer());
        loan.setProduct(product());
        loan.setStatus(LoanStatus.OPEN);
        loan.setPrincipalAmount(new BigDecimal("1000.00"));
        loan.setOutstandingAmount(new BigDecimal("1000.00"));
        loan.setTotalFeesApplied(BigDecimal.ZERO);
        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));
        when(repaymentRepository.save(any(Repayment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RepaymentResponse response = service.repay(10L, new RepaymentRequest(
                new BigDecimal("1000.00"),
                "MPESA",
                "EXT-001"));

        assertThat(response.amount()).isEqualByComparingTo("1000.00");
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.CLOSED);
        assertThat(loan.getOutstandingAmount()).isEqualByComparingTo("0.00");
    }

    @Test
    void repaymentTracksOverpaymentSeparatelyFromAppliedAmount() {
        Loan loan = new Loan();
        loan.setLoanNumber("LN-OVERPAY");
        loan.setCustomer(customer());
        loan.setProduct(product());
        loan.setStatus(LoanStatus.OPEN);
        loan.setPrincipalAmount(new BigDecimal("1000.00"));
        loan.setOutstandingAmount(new BigDecimal("1000.00"));
        loan.setTotalFeesApplied(BigDecimal.ZERO);
        when(loanRepository.findById(11L)).thenReturn(Optional.of(loan));
        when(repaymentRepository.save(any(Repayment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repaymentRepository.findByLoanId(11L)).thenAnswer(invocation -> java.util.List.of(
                new Repayment() {{
                    setAmount(new BigDecimal("1500.00"));
                    setAppliedAmount(new BigDecimal("1000.00"));
                }}
        ));

        RepaymentResponse response = service.repay(11L, new RepaymentRequest(
                new BigDecimal("1500.00"),
                "MPESA",
                "EXT-OVERPAY"));

        assertThat(response.amount()).isEqualByComparingTo("1500.00");
        assertThat(response.appliedAmount()).isEqualByComparingTo("1000.00");
        assertThat(response.overpaymentAmount()).isEqualByComparingTo("500.00");
        assertThat(loan.getOutstandingAmount()).isEqualByComparingTo("0.00");
    }

    private Customer customer() {
        Customer customer = new Customer();
        customer.setCustomerNumber("CUST-TEST");
        customer.setFirstName("Test");
        customer.setLastName("Customer");
        customer.setEmail("test@example.com");
        customer.setPhoneNumber("+254700000000");
        customer.setLoanLimit(new BigDecimal("50000.00"));
        customer.setRiskGrade("A");
        customer.setPreferredNotificationChannel("EMAIL");
        return customer;
    }

    private LoanProduct product() {
        LoanProduct product = new LoanProduct();
        product.setCode("TEST");
        product.setName("Test Product");
        product.setMinimumAmount(new BigDecimal("100.00"));
        product.setMaximumAmount(new BigDecimal("50000.00"));
        product.setTenureValue(30);
        product.setTenureUnit(TenureUnit.DAYS);
        product.setDaysAfterDueForFeeApplication(3);
        product.setActive(true);

        ProductFee fee = new ProductFee();
        fee.setFeeType(FeeType.SERVICE_FEE);
        fee.setCalculationType(FeeCalculationType.PERCENTAGE);
        fee.setApplicationStage(FeeApplicationStage.ORIGINATION);
        fee.setValue(new BigDecimal("5.00"));
        product.addFee(fee);
        return product;
    }

    private Loan loan(Customer customer, LoanProduct product, BigDecimal outstandingAmount) {
        Loan loan = new Loan();
        loan.setLoanNumber("LN-TEST");
        loan.setCustomer(customer);
        loan.setProduct(product);
        loan.setStructure(LoanStructure.LUMP_SUM);
        loan.setStatus(LoanStatus.OPEN);
        loan.setPrincipalAmount(outstandingAmount);
        loan.setOutstandingAmount(outstandingAmount);
        loan.setTotalFeesApplied(BigDecimal.ZERO);
        loan.setOriginationDate(LocalDate.now().minusDays(30));
        loan.setDueDate(LocalDate.now());
        return loan;
    }
}
