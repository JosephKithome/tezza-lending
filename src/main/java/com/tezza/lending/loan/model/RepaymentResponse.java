package com.tezza.lending.loan.model;

import java.math.BigDecimal;
import java.time.Instant;

public record RepaymentResponse(
        Long id,
        String loanNumber,
        BigDecimal amount,
        BigDecimal appliedAmount,
        BigDecimal overpaymentAmount,
        String channel,
        String externalReference,
        Instant paidAt
) {
    public static RepaymentResponse from(Repayment repayment) {
        return new RepaymentResponse(
                repayment.getId(),
                repayment.getLoan().getLoanNumber(),
                repayment.getAmount(),
                repayment.getAppliedAmount(),
                repayment.getOverpaymentAmount(),
                repayment.getChannel(),
                repayment.getExternalReference(),
                repayment.getPaidAt());
    }
}
