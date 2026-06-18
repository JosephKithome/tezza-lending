package com.tezza.lending.loan.repository;

import com.tezza.lending.loan.model.Loan;
import com.tezza.lending.loan.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByLoanNumber(String loanNumber);

    List<Loan> findByStatusInAndDueDateBefore(List<LoanStatus> statuses, LocalDate businessDate);

    List<Loan> findByCustomerId(Long customerId);

    long countByCustomerId(Long customerId);

    long countByProductId(Long productId);
}
