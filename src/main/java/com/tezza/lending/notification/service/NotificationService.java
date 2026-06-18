package com.tezza.lending.notification.service;

import com.tezza.lending.customer.model.Customer;
import com.tezza.lending.loan.model.Loan;
import com.tezza.lending.notification.model.NotificationEventType;
import com.tezza.lending.notification.model.NotificationResponse;
import com.tezza.lending.notification.model.RuleRequest;
import com.tezza.lending.notification.model.RuleResponse;
import com.tezza.lending.notification.model.TemplateRequest;
import com.tezza.lending.notification.model.TemplateResponse;
import com.tezza.lending.shared.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface NotificationService {
    TemplateResponse createTemplate(TemplateRequest request);

    RuleResponse createRule(RuleRequest request);

    NotificationResponse publishCustomerRegistration(Customer customer);

    NotificationResponse publish(NotificationEventType eventType, Customer customer, Loan loan);

    NotificationResponse publish(
            NotificationEventType eventType,
            Customer customer,
            Loan loan,
            Map<String, String> extraVariables);

    NotificationResponse publishEmail(
            NotificationEventType eventType,
            Customer customer,
            Loan loan,
            Map<String, String> extraVariables);

    PagedResponse list(Pageable pageable);

    void deleteNotification(Long id);

    void deleteTemplate(Long id);

    void deleteRule(Long id);
}
