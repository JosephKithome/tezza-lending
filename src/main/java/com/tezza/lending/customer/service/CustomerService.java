package com.tezza.lending.customer.service;

import com.tezza.lending.customer.model.CustomerRequest;
import com.tezza.lending.customer.model.CustomerResponse;
import com.tezza.lending.customer.model.LoanLimitRequest;
import com.tezza.lending.shared.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    CustomerResponse create(CustomerRequest request);

    CustomerResponse updateLimit(Long id, LoanLimitRequest request);

    CustomerResponse get(Long id);

    PagedResponse list(Pageable pageable);

    void delete(Long id);
}
