package com.tezza.lending.notification.serviceimpl;

import com.tezza.lending.notification.model.GmailNotificationProperties;
import com.tezza.lending.notification.model.NotificationChannel;
import com.tezza.lending.notification.model.NotificationDeliveryRequest;
import com.tezza.lending.notification.model.NotificationDeliveryResult;
import com.tezza.lending.notification.service.NotificationSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class GmailEmailNotificationSender implements NotificationSender {
    private static final Logger log = LoggerFactory.getLogger(GmailEmailNotificationSender.class);

    private final JavaMailSender mailSender;
    private final GmailNotificationProperties properties;

    public GmailEmailNotificationSender(JavaMailSender mailSender, GmailNotificationProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.EMAIL;
    }

    @Override
    public NotificationDeliveryResult send(NotificationDeliveryRequest request) {
        if (!properties.enabled()) {
            return NotificationDeliveryResult.failed("Email delivery disabled. Set GMAIL_NOTIFICATIONS_ENABLED=true to send via Gmail.");
        }
        if (isBlank(properties.from())) {
            return NotificationDeliveryResult.failed("Gmail sender is not configured. Set GMAIL_USERNAME or GMAIL_FROM.");
        }
        if (isBlank(request.recipient())) {
            return NotificationDeliveryResult.failed("Email recipient is blank.");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(properties.from());
            helper.setTo(request.recipient());
            helper.setSubject(request.subject());
            helper.setText(request.message(), request.html());
            mailSender.send(message);
            return NotificationDeliveryResult.delivered("Email sent through Gmail SMTP.");
        } catch (MailException | MessagingException ex) {
            log.warn("Failed to send Gmail notification to {}", request.recipient(), ex);
            return NotificationDeliveryResult.failed(ex.getMessage());
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
