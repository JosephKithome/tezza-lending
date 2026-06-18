package com.tezza.lending.customer.controller;

import com.tezza.lending.customer.model.CustomerRequest;
import com.tezza.lending.customer.model.LoanLimitRequest;
import com.tezza.lending.customer.service.CustomerService;
import com.tezza.lending.logging.Helper;
import com.tezza.lending.shared.PageableFactory;
import com.tezza.lending.shared.RequestContext;
import com.tezza.lending.shared.ResponsePayload;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final Bucket bucket = Bucket.builder()
            .addLimit(Bandwidth.builder()
                    .capacity(300)
                    .refillGreedy(300, Duration.ofSeconds(1))
                    .build())
            .build();

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a customer profile")
    public Object create(@Valid @RequestBody CustomerRequest request) {
        if (rateLimitExceeded()) {
            return Helper.tooManyRequests();
        }
        ResponsePayload response = ResponsePayload.created(
                RequestContext.requestId(),
                "Customer created",
                customerService.create(request));
        Helper.logger(log, "POST", "/api/v1/customers", HttpStatus.CREATED.value(), request, response);
        return response;
    }

    @PatchMapping("/{id}/loan-limit")
    @Operation(summary = "Update a customer loan limit")
    public Object updateLimit(
            @PathVariable Long id,
            @Valid @RequestBody LoanLimitRequest request) {
        if (rateLimitExceeded()) {
            return Helper.tooManyRequests();
        }
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Loan limit updated",
                customerService.updateLimit(id, request));
        Helper.logger(log, "PATCH", "/api/v1/customers/" + id + "/loan-limit", HttpStatus.OK.value(), request, response);
        return response;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one customer")
    public Object get(@PathVariable Long id) {
        if (rateLimitExceeded()) {
            return Helper.tooManyRequests();
        }
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Customer found",
                customerService.get(id));
        Helper.logger(log, "GET", "/api/v1/customers/" + id, HttpStatus.OK.value(), "id=" + id, response);
        return response;
    }

    @GetMapping
    @Operation(summary = "List customers")
    public Object list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        String requestPayload = PageableFactory.requestPayload(page, size, sortBy, sortDirection);
        if (rateLimitExceeded()) {
            return Helper.tooManyRequests();
        }
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Customers found",
                customerService.list(PageableFactory.of(page, size, sortBy, sortDirection)));

        Helper.logger(log, "GET", "/api/v1/customers", HttpStatus.OK.value(), requestPayload, response);
        
        return response;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer profile")
    public Object delete(@PathVariable Long id) {
        if (rateLimitExceeded()) {
            return Helper.tooManyRequests();
        }
        customerService.delete(id);
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Customer deleted",
                null);
        Helper.logger(log, "DELETE", "/api/v1/customers/" + id, HttpStatus.OK.value(), "id=" + id, response);
        return response;
    }

    private boolean rateLimitExceeded() {
        if (bucket.tryConsume(1)) {
            return false;
        }
        log.warn("Rate limit exceeded");
        return true;
    }
}
