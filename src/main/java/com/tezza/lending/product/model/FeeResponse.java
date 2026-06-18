package com.tezza.lending.product.model;

import java.math.BigDecimal;

public record FeeResponse(
        Long id,
        FeeType feeType,
        FeeCalculationType calculationType,
        FeeApplicationStage applicationStage,
        BigDecimal value,
        Integer triggerDaysAfterDue
) {
    static FeeResponse from(ProductFee fee) {
        return new FeeResponse(
                fee.getId(),
                fee.getFeeType(),
                fee.getCalculationType(),
                fee.getApplicationStage(),
                fee.getValue(),
                fee.getTriggerDaysAfterDue());
    }
}
