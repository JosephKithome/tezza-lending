package com.tezza.lending.product.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(
        String code,
        @NotBlank String name,
        @NotNull @DecimalMin("0.00") BigDecimal minimumAmount,
        @NotNull @DecimalMin("0.00") BigDecimal maximumAmount,
        @NotNull @Positive Integer tenureValue,
        @NotNull TenureUnit tenureUnit,
        @NotNull @Positive Integer daysAfterDueForFeeApplication,
        Boolean active,
        @Valid List<FeeRequest> fees
) {
}
