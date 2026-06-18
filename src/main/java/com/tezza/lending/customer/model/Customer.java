package com.tezza.lending.customer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private BigDecimal loanLimit;
    private String riskGrade;
    private String preferredNotificationChannel;
    private String registrationOtp;
    private Instant registrationOtpExpiresAt;

    public Long getId() {
        return id;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public BigDecimal getLoanLimit() {
        return loanLimit;
    }

    public void setLoanLimit(BigDecimal loanLimit) {
        this.loanLimit = loanLimit;
    }

    public String getRiskGrade() {
        return riskGrade;
    }

    public void setRiskGrade(String riskGrade) {
        this.riskGrade = riskGrade;
    }

    public String getPreferredNotificationChannel() {
        return preferredNotificationChannel;
    }

    public void setPreferredNotificationChannel(String preferredNotificationChannel) {
        this.preferredNotificationChannel = preferredNotificationChannel;
    }

    public String getRegistrationOtp() {
        return registrationOtp;
    }

    public void setRegistrationOtp(String registrationOtp) {
        this.registrationOtp = registrationOtp;
    }

    public Instant getRegistrationOtpExpiresAt() {
        return registrationOtpExpiresAt;
    }

    public void setRegistrationOtpExpiresAt(Instant registrationOtpExpiresAt) {
        this.registrationOtpExpiresAt = registrationOtpExpiresAt;
    }
}
