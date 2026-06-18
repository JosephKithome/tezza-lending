package com.tezza.lending.loan.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RepaymentRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        String channel,
        String externalReference
) {
}
