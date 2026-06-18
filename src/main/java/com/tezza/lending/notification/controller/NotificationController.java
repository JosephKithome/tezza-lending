package com.tezza.lending.notification.controller;

import com.tezza.lending.logging.Helper;
import com.tezza.lending.notification.model.RuleRequest;
import com.tezza.lending.notification.model.TemplateRequest;
import com.tezza.lending.notification.service.NotificationService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/templates")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a notification template")
    public ResponsePayload createTemplate(@Valid @RequestBody TemplateRequest request) {
        ResponsePayload response = ResponsePayload.created(
                RequestContext.requestId(),
                "Template created",
                notificationService.createTemplate(request));
        Helper.logger(log, "POST", "/api/v1/notifications/templates", HttpStatus.CREATED.value(), request, response);
        return response;
    }

    @PostMapping("/rules")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a notification routing rule")
    public ResponsePayload createRule(@Valid @RequestBody RuleRequest request) {
        ResponsePayload response = ResponsePayload.created(
                RequestContext.requestId(),
                "Rule created",
                notificationService.createRule(request));
        Helper.logger(log, "POST", "/api/v1/notifications/rules", HttpStatus.CREATED.value(), request, response);
        return response;
    }

    @GetMapping
    @Operation(summary = "List generated notifications")
    public ResponsePayload list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        String requestPayload = PageableFactory.requestPayload(page, size, sortBy, sortDirection);
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Notifications found",
                notificationService.list(PageableFactory.of(page, size, sortBy, sortDirection)));
        Helper.logger(log, "GET", "/api/v1/notifications", HttpStatus.OK.value(), requestPayload, response);
        return response;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a generated notification")
    public ResponsePayload deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Notification deleted",
                null);
        Helper.logger(log, "DELETE", "/api/v1/notifications/" + id, HttpStatus.OK.value(), "id=" + id, response);
        return response;
    }

    @DeleteMapping("/templates/{id}")
    @Operation(summary = "Delete a notification template")
    public ResponsePayload deleteTemplate(@PathVariable Long id) {
        notificationService.deleteTemplate(id);
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Template deleted",
                null);
        Helper.logger(log, "DELETE", "/api/v1/notifications/templates/" + id, HttpStatus.OK.value(), "id=" + id, response);
        return response;
    }

    @DeleteMapping("/rules/{id}")
    @Operation(summary = "Delete a notification routing rule")
    public ResponsePayload deleteRule(@PathVariable Long id) {
        notificationService.deleteRule(id);
        ResponsePayload response = ResponsePayload.ok(
                RequestContext.requestId(),
                "Rule deleted",
                null);
        Helper.logger(log, "DELETE", "/api/v1/notifications/rules/" + id, HttpStatus.OK.value(), "id=" + id, response);
        return response;
    }
}
