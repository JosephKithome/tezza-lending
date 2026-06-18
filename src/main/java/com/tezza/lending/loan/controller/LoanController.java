package com.tezza.lending.loan.controller;

import com.tezza.lending.loan.model.LoanRequest;
import com.tezza.lending.loan.model.RepaymentRequest;
import com.tezza.lending.loan.service.LoanService;
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
import org.springframework.format.annotation.DateTimeFormat;
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
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {
    private static final Logger log = LoggerFactory.getLogger(LoanController.class);
    private final Bucket bucket = Bucket.builder()
            .addLimit(Bandwidth.builder()
                    .capacity(300)
                    .refillGreedy(300, Duration.ofSeconds(1))
                    .build())
            .build();

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Disburse a loan")
    public Object disburse(@Valid @RequestBody LoanRequest request) {
        if (rateLimitExceeded()) {
            return Helper.tooManyRequests();
        }
        ResponsePayload response = ResponsePayload.created(
                RequestContext.requestId(),
                "Loan disbursed",
                loanService.disburse(request));
        Helper.logger(log, "POST", "/api/v1/loans", HttpStatus.CREATED.value(), request, response);
        return response;
    }

    @PostMapping("/{id}/repayments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Record a loan repayment")
    public Object repay(
            @PathVariable Long id,
            @Valid @RequestBody RepaymentRequest request) {
        if (rateLimitExceeded()) {
            return Helper.tooManyRequests();
        }
        ResponsePayload response = ResponsePayload.created(
                RequestContext.requestId(),
                "Repayment recorded",
                loanService.repay(id, request));
        Helper.logger(log, "POST", "/api/v1/loans/" + id + "/repayments", HttpStatus.CREATED.value(), request, response);
        return response;
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel an open or overdue loan")
    public Object cancel(@PathVariable Long id) {
        if (rateLimitExceeded()) {
            return Helper.tooManyRequests();
        }
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Loan cancelled",
                loanService.cancel(id));
        Helper.logger(log, "PATCH", "/api/v1/loans/" + id + "/cancel", HttpStatus.OK.value(), "id=" + id, response);
        return response;
    }

    @PatchMapping("/{id}/write-off")
    @Operation(summary = "Write off a loan")
    public Object writeOff(@PathVariable Long id) {
        if (rateLimitExceeded()) {
            return Helper.tooManyRequests();
        }
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Loan written off",
                loanService.writeOff(id));
        Helper.logger(log, "PATCH", "/api/v1/loans/" + id + "/write-off", HttpStatus.OK.value(), "id=" + id, response);
        return response;
    }

    @PostMapping("/sweeps/overdue")
    @Operation(summary = "Run the overdue sweep manually for a business date")
    public Object sweepOverdue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate businessDate) {
        LocalDate date = businessDate == null ? LocalDate.now() : businessDate;
        if (rateLimitExceeded()) {
            return Helper.tooManyRequests();
        }
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Overdue sweep completed",
                loanService.sweepOverdue(date));
        Helper.logger(log, "POST", "/api/v1/loans/sweeps/overdue", HttpStatus.OK.value(), "businessDate=" + date, response);
        return response;
    }

    @PostMapping("/sweeps/due-reminders")
    @Operation(summary = "Send due date reminders for loans due soon")
    public Object sendDueDateReminders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate businessDate,
            @RequestParam(defaultValue = "3") int daysAhead) {
        LocalDate date = businessDate == null ? LocalDate.now() : businessDate;
        if (rateLimitExceeded()) {
            return Helper.tooManyRequests();
        }
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Due date reminders completed",
                loanService.sendDueDateReminders(date, daysAhead));
        String requestPayload = "businessDate=" + date + ", daysAhead=" + daysAhead;
        Helper.logger(log, "POST", "/api/v1/loans/sweeps/due-reminders", HttpStatus.OK.value(), requestPayload, response);
        return response;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one loan")
    public Object get(@PathVariable Long id) {
        if (rateLimitExceeded()) {
            return Helper.tooManyRequests();
        }
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Loan found",
                loanService.get(id));
        Helper.logger(log, "GET", "/api/v1/loans/" + id, HttpStatus.OK.value(), "id=" + id, response);
        return response;
    }

    @GetMapping
    @Operation(summary = "List loans")
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
                "Loans found",
                loanService.list(PageableFactory.of(page, size, sortBy, sortDirection)));
        Helper.logger(log, "GET", "/api/v1/loans", HttpStatus.OK.value(), requestPayload, response);
        return response;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a loan without repayments")
    public Object delete(@PathVariable Long id) {
        if (rateLimitExceeded()) {
            return Helper.tooManyRequests();
        }
        loanService.delete(id);
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Loan deleted",
                null);
        Helper.logger(log, "DELETE", "/api/v1/loans/" + id, HttpStatus.OK.value(), "id=" + id, response);
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
