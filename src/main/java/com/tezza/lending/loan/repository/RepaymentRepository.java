package com.tezza.lending.loan.repository;

import com.tezza.lending.loan.model.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepaymentRepository extends JpaRepository<Repayment, Long> {
    List<Repayment> findByLoanId(Long loanId);

    long countByLoanId(Long loanId);
}
