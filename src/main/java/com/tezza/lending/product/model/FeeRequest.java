package com.tezza.lending.product.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FeeRequest(
        @NotNull FeeType feeType,
        @NotNull FeeCalculationType calculationType,
        @NotNull FeeApplicationStage applicationStage,
        @NotNull @DecimalMin("0.00") BigDecimal value,
        Integer triggerDaysAfterDue
) {
}
