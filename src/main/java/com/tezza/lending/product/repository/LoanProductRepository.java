package com.tezza.lending.product.repository;

import com.tezza.lending.product.model.*;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
    Optional<LoanProduct> findByCode(String code);
}
