package com.tezza.lending.loan.model;

import com.tezza.lending.customer.model.Customer;
import com.tezza.lending.product.model.LoanProduct;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String loanNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private LoanProduct product;
    @Enumerated(EnumType.STRING)
    private LoanStructure structure;
    @Enumerated(EnumType.STRING)
    private LoanStatus status;
    private BigDecimal principalAmount;
    private BigDecimal outstandingAmount;
    private BigDecimal totalFeesApplied;
    private LocalDate originationDate;
    private LocalDate dueDate;
    private LocalDate consolidatedDueDate;
    private boolean consolidatedBilling;
    private LocalDate lastFeeAccrualDate;
    private boolean lateFeeApplied;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanInstallment> installments = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LoanProduct getProduct() {
        return product;
    }

    public void setProduct(LoanProduct product) {
        this.product = product;
    }

    public LoanStructure getStructure() {
        return structure;
    }

    public void setStructure(LoanStructure structure) {
        this.structure = structure;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(BigDecimal outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public BigDecimal getTotalFeesApplied() {
        return totalFeesApplied;
    }

    public void setTotalFeesApplied(BigDecimal totalFeesApplied) {
        this.totalFeesApplied = totalFeesApplied;
    }

    public LocalDate getOriginationDate() {
        return originationDate;
    }

    public void setOriginationDate(LocalDate originationDate) {
        this.originationDate = originationDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getConsolidatedDueDate() {
        return consolidatedDueDate;
    }

    public void setConsolidatedDueDate(LocalDate consolidatedDueDate) {
        this.consolidatedDueDate = consolidatedDueDate;
    }

    public boolean isConsolidatedBilling() {
        return consolidatedBilling;
    }

    public void setConsolidatedBilling(boolean consolidatedBilling) {
        this.consolidatedBilling = consolidatedBilling;
    }

    public LocalDate getLastFeeAccrualDate() {
        return lastFeeAccrualDate;
    }

    public void setLastFeeAccrualDate(LocalDate lastFeeAccrualDate) {
        this.lastFeeAccrualDate = lastFeeAccrualDate;
    }

    public boolean isLateFeeApplied() {
        return lateFeeApplied;
    }

    public void setLateFeeApplied(boolean lateFeeApplied) {
        this.lateFeeApplied = lateFeeApplied;
    }

    public List<LoanInstallment> getInstallments() {
        return installments;
    }

    public void addInstallment(LoanInstallment installment) {
        installment.setLoan(this);
        this.installments.add(installment);
    }
}
