package com.tezza.lending.loan.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record LoanResponse(
        Long id,
        String loanNumber,
        Long customerId,
        Long productId,
        LoanStructure structure,
        LoanStatus status,
        BigDecimal principalAmount,
        BigDecimal outstandingAmount,
        BigDecimal totalFeesApplied,
        LocalDate originationDate,
        LocalDate dueDate,
        boolean consolidatedBilling,
        LocalDate consolidatedDueDate,
        LocalDate lastFeeAccrualDate,
        boolean lateFeeApplied,
        boolean dueDateReminderSent,
        List<InstallmentResponse> installments
) {
    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getLoanNumber(),
                loan.getCustomer().getId(),
                loan.getProduct().getId(),
                loan.getStructure(),
                loan.getStatus(),
                loan.getPrincipalAmount(),
                loan.getOutstandingAmount(),
                loan.getTotalFeesApplied(),
                loan.getOriginationDate(),
                loan.getDueDate(),
                loan.isConsolidatedBilling(),
                loan.getConsolidatedDueDate(),
                loan.getLastFeeAccrualDate(),
                loan.isLateFeeApplied(),
                loan.isDueDateReminderSent(),
                loan.getInstallments().stream().map(InstallmentResponse::from).toList());
    }
}
