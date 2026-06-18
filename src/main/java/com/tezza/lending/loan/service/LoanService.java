package com.tezza.lending.loan.service;

import com.tezza.lending.loan.model.LoanRequest;
import com.tezza.lending.loan.model.LoanResponse;
import com.tezza.lending.loan.model.RepaymentRequest;
import com.tezza.lending.loan.model.RepaymentResponse;
import com.tezza.lending.shared.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface LoanService {
    LoanResponse disburse(LoanRequest request);

    RepaymentResponse repay(Long loanId, RepaymentRequest request);

    LoanResponse cancel(Long loanId);

    LoanResponse writeOff(Long loanId);

    List<LoanResponse> sweepOverdue(LocalDate businessDate);

    LoanResponse get(Long id);

    PagedResponse list(Pageable pageable);

    void delete(Long id);
}
