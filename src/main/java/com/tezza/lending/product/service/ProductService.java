package com.tezza.lending.product.service;

import com.tezza.lending.product.model.ProductRequest;
import com.tezza.lending.product.model.ProductResponse;
import com.tezza.lending.shared.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    ProductResponse get(Long id);

    PagedResponse list(Pageable pageable);

    void delete(Long id);
}
