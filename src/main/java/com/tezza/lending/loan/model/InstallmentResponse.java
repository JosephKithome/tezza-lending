package com.tezza.lending.loan.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InstallmentResponse(
        Long id,
        Integer installmentNumber,
        LocalDate dueDate,
        BigDecimal amountDue,
        BigDecimal amountPaid,
        boolean paid
) {
    static InstallmentResponse from(LoanInstallment installment) {
        return new InstallmentResponse(
                installment.getId(),
                installment.getInstallmentNumber(),
                installment.getDueDate(),
                installment.getAmountDue(),
                installment.getAmountPaid(),
                installment.isPaid());
    }
}
