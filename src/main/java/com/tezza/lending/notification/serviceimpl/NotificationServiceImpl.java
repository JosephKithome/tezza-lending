package com.tezza.lending.notification.serviceimpl;

import com.tezza.lending.customer.model.Customer;
import com.tezza.lending.exception.BusinessRuleException;
import com.tezza.lending.loan.model.Loan;
import com.tezza.lending.notification.model.Notification;
import com.tezza.lending.notification.model.NotificationChannel;
import com.tezza.lending.notification.model.NotificationEventType;
import com.tezza.lending.notification.model.NotificationRule;
import com.tezza.lending.notification.model.NotificationStatus;
import com.tezza.lending.notification.model.NotificationTemplate;
import com.tezza.lending.notification.model.NotificationResponse;
import com.tezza.lending.notification.model.RuleRequest;
import com.tezza.lending.notification.model.RuleResponse;
import com.tezza.lending.notification.model.TemplateRequest;
import com.tezza.lending.notification.model.TemplateResponse;
import com.tezza.lending.notification.repository.NotificationRepository;
import com.tezza.lending.notification.repository.NotificationRuleRepository;
import com.tezza.lending.notification.repository.NotificationTemplateRepository;
import com.tezza.lending.notification.service.AsyncNotificationDeliveryService;
import com.tezza.lending.notification.service.NotificationService;
import com.tezza.lending.shared.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationRuleRepository ruleRepository;
    private final AsyncNotificationDeliveryService deliveryService;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   NotificationTemplateRepository templateRepository,
                                   NotificationRuleRepository ruleRepository,
                                   AsyncNotificationDeliveryService deliveryService) {
        this.notificationRepository = notificationRepository;
        this.templateRepository = templateRepository;
        this.ruleRepository = ruleRepository;
        this.deliveryService = deliveryService;
    }

    @Override
    public TemplateResponse createTemplate(TemplateRequest request) {
        NotificationTemplate template = new NotificationTemplate();
        template.setEventType(request.eventType());
        template.setChannel(request.channel());
        template.setSubject(request.subject());
        template.setBody(request.body());
        template.setActive(request.active());
        return TemplateResponse.from(templateRepository.save(template));
    }

    @Override
    public RuleResponse createRule(RuleRequest request) {
        NotificationRule rule = new NotificationRule();
        rule.setEventType(request.eventType());
        rule.setProductCode(request.productCode());
        rule.setCustomerSegment(request.customerSegment());
        rule.setChannel(request.channel());
        rule.setActive(request.active());
        return RuleResponse.from(ruleRepository.save(rule));
    }

    @Override
    public NotificationResponse publishCustomerRegistration(Customer customer) {
        return publish(NotificationEventType.CUSTOMER_REGISTERED, NotificationChannel.EMAIL, customer, null, Map.of());
    }

    @Override
    public NotificationResponse publish(NotificationEventType eventType, Customer customer, Loan loan) {
        return publish(eventType, customer, loan, Map.of());
    }

    @Override
    public NotificationResponse publish(
            NotificationEventType eventType,
            Customer customer,
            Loan loan,
            Map<String, String> extraVariables) {
        return publish(eventType, resolveChannel(eventType, customer, loan), customer, loan, extraVariables);
    }

    @Override
    public NotificationResponse publishEmail(
            NotificationEventType eventType,
            Customer customer,
            Loan loan,
            Map<String, String> extraVariables) {
        return publish(eventType, NotificationChannel.EMAIL, customer, loan, extraVariables);
    }

    private NotificationResponse publish(
            NotificationEventType eventType,
            NotificationChannel channel,
            Customer customer,
            Loan loan,
            Map<String, String> extraVariables) {
        NotificationTemplate template = templateRepository.findTopByEventTypeAndChannelAndActiveTrueOrderByIdDesc(eventType, channel)
                .orElseThrow(() -> new BusinessRuleException(
                        "No active notification template configured for " + eventType + " on " + channel));
        Map<String, String> variables = new HashMap<>();
        variables.put("firstName", customer.getFirstName());
        variables.put("lastName", customer.getLastName());
        variables.put("customerNumber", customer.getCustomerNumber());
        variables.put("otp", customer.getRegistrationOtp() == null ? "" : customer.getRegistrationOtp());
        variables.put("otpExpiresAt", customer.getRegistrationOtpExpiresAt() == null
                ? ""
                : customer.getRegistrationOtpExpiresAt().toString());
        variables.put("loanNumber", loan == null ? "" : loan.getLoanNumber());
        variables.put("productName", loan == null ? "" : loan.getProduct().getName());
        variables.put("principalAmount", loan == null ? "" : loan.getPrincipalAmount().toPlainString());
        variables.put("amount", loan == null ? "" : loan.getOutstandingAmount().toPlainString());
        variables.put("dueDate", loan == null ? "" : loan.getDueDate().toString());
        variables.put("structure", loan == null ? "" : loan.getStructure().name());
        variables.putAll(extraVariables);

        Notification notification = new Notification();
        notification.setCustomerId(customer.getId());
        notification.setLoanId(loan == null ? null : loan.getId());
        notification.setEventType(eventType);
        notification.setChannel(channel);
        notification.setRecipient(recipient(customer, channel));
        notification.setSubject(render(template.getSubject(), variables));
        notification.setMessage(render(template.getBody(), variables));
        notification.setCreatedAt(Instant.now());
        notification.setStatus(NotificationStatus.QUEUED);

        Notification saved = notificationRepository.save(notification);
        deliverAfterCommit(saved.getId());
        return NotificationResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse list(Pageable pageable) {
        return PagedResponse.from(notificationRepository.findAll(pageable).map(NotificationResponse::from));
    }

    @Override
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new com.tezza.lending.exception.ResourceNotFoundException("Notification not found"));
        notificationRepository.delete(notification);
    }

    @Override
    public void deleteTemplate(Long id) {
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new com.tezza.lending.exception.ResourceNotFoundException("Notification template not found"));
        templateRepository.delete(template);
    }

    @Override
    public void deleteRule(Long id) {
        NotificationRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new com.tezza.lending.exception.ResourceNotFoundException("Notification rule not found"));
        ruleRepository.delete(rule);
    }

    private NotificationChannel resolveChannel(NotificationEventType eventType, Customer customer, Loan loan) {
        String productCode = loan == null ? null : loan.getProduct().getCode();
        return ruleRepository.findFirstByEventTypeAndProductCodeAndCustomerSegmentAndActiveTrue(
                        eventType, productCode, customer.getRiskGrade())
                .map(NotificationRule::getChannel)
                .orElseGet(() -> NotificationChannel.valueOf(customer.getPreferredNotificationChannel().toUpperCase()));
    }

    private String recipient(Customer customer, NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> customer.getEmail();
            case SMS, PUSH -> customer.getPhoneNumber();
        };
    }

    private String render(String template, Map<String, String> variables) {
        String rendered = template == null ? "" : template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            rendered = rendered.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return rendered;
    }

    private void deliverAfterCommit(Long notificationId) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            deliveryService.deliver(notificationId);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deliveryService.deliver(notificationId);
            }
        });
    }
}
