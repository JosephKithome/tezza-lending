package com.tezza.lending.customer.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoanLimitRequest(@NotNull @DecimalMin("0.00") BigDecimal loanLimit) {
}
