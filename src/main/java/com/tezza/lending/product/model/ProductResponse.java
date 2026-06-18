package com.tezza.lending.product.model;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String code,
        String name,
        BigDecimal minimumAmount,
        BigDecimal maximumAmount,
        Integer tenureValue,
        TenureUnit tenureUnit,
        Integer daysAfterDueForFeeApplication,
        boolean active,
        List<FeeResponse> fees
) {
    public static ProductResponse from(LoanProduct product) {
        return new ProductResponse(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getMinimumAmount(),
                product.getMaximumAmount(),
                product.getTenureValue(),
                product.getTenureUnit(),
                product.getDaysAfterDueForFeeApplication(),
                product.isActive(),
                product.getFees().stream().map(FeeResponse::from).toList());
    }
}
