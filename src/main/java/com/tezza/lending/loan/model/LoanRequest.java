package com.tezza.lending.loan.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanRequest(
        @NotNull Long customerId,
        @NotNull Long productId,
        @NotNull @DecimalMin("1.00") BigDecimal principalAmount,
        LoanStructure structure,
        @Positive Integer installmentCount,
        boolean consolidatedBilling,
        LocalDate consolidatedDueDate
) {
}
