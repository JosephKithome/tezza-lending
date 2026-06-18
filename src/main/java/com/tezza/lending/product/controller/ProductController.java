package com.tezza.lending.product.controller;

import com.tezza.lending.logging.Helper;
import com.tezza.lending.product.model.ProductRequest;
import com.tezza.lending.product.service.ProductService;
import com.tezza.lending.shared.PageableFactory;
import com.tezza.lending.shared.RequestContext;
import com.tezza.lending.shared.ResponsePayload;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a loan product with tenure and fee configuration")
    public ResponsePayload create(@Valid @RequestBody ProductRequest request) {
        ResponsePayload response = ResponsePayload.created(
                RequestContext.requestId(),
                "Product created",
                productService.create(request));
        Helper.logger(log, "POST", "/api/v1/products", HttpStatus.CREATED.value(), request, response);
        return response;
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a loan product")
    public ResponsePayload update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Product updated",
                productService.update(id, request));
        Helper.logger(log, "PUT", "/api/v1/products/" + id, HttpStatus.OK.value(), request, response);
        return response;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one loan product")
    public ResponsePayload get(@PathVariable Long id) {
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Product found",
                productService.get(id));
        Helper.logger(log, "GET", "/api/v1/products/" + id, HttpStatus.OK.value(), "id=" + id, response);
        return response;
    }

    @GetMapping
    @Operation(summary = "List loan products")
    public ResponsePayload list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        String requestPayload = PageableFactory.requestPayload(page, size, sortBy, sortDirection);
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Products found",
                productService.list(PageableFactory.of(page, size, sortBy, sortDirection)));
        Helper.logger(log, "GET", "/api/v1/products", HttpStatus.OK.value(), requestPayload, response);
        return response;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a loan product")
    public ResponsePayload delete(@PathVariable Long id) {
        productService.delete(id);
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Product deleted",
                null);
        Helper.logger(log, "DELETE", "/api/v1/products/" + id, HttpStatus.OK.value(), "id=" + id, response);
        return response;
    }
}
