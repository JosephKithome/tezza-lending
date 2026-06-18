package com.tezza.lending.product.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_products")
public class LoanProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    private BigDecimal minimumAmount;
    private BigDecimal maximumAmount;
    private Integer tenureValue;
    @Enumerated(EnumType.STRING)
    private TenureUnit tenureUnit;
    private Integer daysAfterDueForFeeApplication;
    private boolean active = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductFee> fees = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(BigDecimal minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public BigDecimal getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(BigDecimal maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public Integer getTenureValue() {
        return tenureValue;
    }

    public void setTenureValue(Integer tenureValue) {
        this.tenureValue = tenureValue;
    }

    public TenureUnit getTenureUnit() {
        return tenureUnit;
    }

    public void setTenureUnit(TenureUnit tenureUnit) {
        this.tenureUnit = tenureUnit;
    }

    public Integer getDaysAfterDueForFeeApplication() {
        return daysAfterDueForFeeApplication;
    }

    public void setDaysAfterDueForFeeApplication(Integer daysAfterDueForFeeApplication) {
        this.daysAfterDueForFeeApplication = daysAfterDueForFeeApplication;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<ProductFee> getFees() {
        return fees;
    }

    public void setFees(List<ProductFee> fees) {
        this.fees.clear();
        fees.forEach(this::addFee);
    }

    public void addFee(ProductFee fee) {
        fee.setProduct(this);
        this.fees.add(fee);
    }
}
