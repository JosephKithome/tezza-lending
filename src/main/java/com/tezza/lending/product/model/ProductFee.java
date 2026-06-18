package com.tezza.lending.product.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "product_fees")
public class ProductFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private LoanProduct product;
    @Enumerated(EnumType.STRING)
    private FeeType feeType;
    @Enumerated(EnumType.STRING)
    private FeeCalculationType calculationType;
    @Enumerated(EnumType.STRING)
    private FeeApplicationStage applicationStage;
    @Column(name = "fee_value")
    private BigDecimal value;
    private Integer triggerDaysAfterDue;

    public Long getId() {
        return id;
    }

    public LoanProduct getProduct() {
        return product;
    }

    public void setProduct(LoanProduct product) {
        this.product = product;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    public FeeCalculationType getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(FeeCalculationType calculationType) {
        this.calculationType = calculationType;
    }

    public FeeApplicationStage getApplicationStage() {
        return applicationStage;
    }

    public void setApplicationStage(FeeApplicationStage applicationStage) {
        this.applicationStage = applicationStage;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Integer getTriggerDaysAfterDue() {
        return triggerDaysAfterDue;
    }

    public void setTriggerDaysAfterDue(Integer triggerDaysAfterDue) {
        this.triggerDaysAfterDue = triggerDaysAfterDue;
    }
}
