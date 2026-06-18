package com.tezza.lending.product.serviceimpl;

import com.tezza.lending.exception.BusinessRuleException;
import com.tezza.lending.exception.ResourceNotFoundException;
import com.tezza.lending.loan.repository.LoanRepository;
import com.tezza.lending.product.model.FeeRequest;
import com.tezza.lending.product.model.LoanProduct;
import com.tezza.lending.product.model.ProductFee;
import com.tezza.lending.product.model.ProductRequest;
import com.tezza.lending.product.model.ProductResponse;
import com.tezza.lending.product.repository.LoanProductRepository;
import com.tezza.lending.product.service.ProductService;
import com.tezza.lending.shared.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final LoanProductRepository repository;
    private final LoanRepository loanRepository;

    public ProductServiceImpl(LoanProductRepository repository, LoanRepository loanRepository) {
        this.repository = repository;
        this.loanRepository = loanRepository;
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        String code = isBlank(request.code()) ? generateProductCode() : request.code();
        repository.findByCode(code).ifPresent(existing -> {
            throw new BusinessRuleException("Product code already exists");
        });
        return ProductResponse.from(repository.save(toEntity(new LoanProduct(), request, code)));
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        LoanProduct product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        String code = isBlank(request.code()) ? product.getCode() : request.code();
        repository.findByCode(code)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessRuleException("Product code already exists");
                });
        return ProductResponse.from(repository.save(toEntity(product, request, code)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        return repository.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse list(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable).map(ProductResponse::from));
    }

    @Override
    public void delete(Long id) {
        LoanProduct product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (loanRepository.countByProductId(id) > 0) {
            throw new BusinessRuleException("Product cannot be deleted because loans are linked to it");
        }
        repository.delete(product);
    }

    private LoanProduct toEntity(LoanProduct product, ProductRequest request, String code) {
        if (request.maximumAmount().compareTo(request.minimumAmount()) < 0) {
            throw new BusinessRuleException("Maximum amount must be greater than or equal to minimum amount");
        }
        product.setCode(code);
        product.setName(request.name());
        product.setMinimumAmount(request.minimumAmount());
        product.setMaximumAmount(request.maximumAmount());
        product.setTenureValue(request.tenureValue());
        product.setTenureUnit(request.tenureUnit());
        product.setDaysAfterDueForFeeApplication(request.daysAfterDueForFeeApplication());
        product.setActive(request.active() == null || request.active());
        product.setFees(request.fees() == null ? List.of() : request.fees().stream().map(this::toFee).toList());
        return product;
    }

    private String generateProductCode() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String candidate;
        do {
            candidate = "PROD-" + date + "-" + ThreadLocalRandom.current().nextInt(1000, 9999);
        } while (repository.findByCode(candidate).isPresent());
        return candidate;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private ProductFee toFee(FeeRequest request) {
        ProductFee fee = new ProductFee();
        fee.setFeeType(request.feeType());
        fee.setCalculationType(request.calculationType());
        fee.setApplicationStage(request.applicationStage());
        fee.setValue(request.value());
        fee.setTriggerDaysAfterDue(request.triggerDaysAfterDue());
        return fee;
    }
}
