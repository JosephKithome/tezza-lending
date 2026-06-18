package com.tezza.lending.notification;


import com.tezza.lending.notification.model.GmailNotificationProperties;
import com.tezza.lending.notification.model.NotificationChannel;
import com.tezza.lending.notification.model.NotificationDeliveryRequest;
import com.tezza.lending.notification.model.NotificationDeliveryResult;
import com.tezza.lending.notification.serviceimpl.GmailEmailNotificationSender;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GmailEmailNotificationSenderTest {
    private final JavaMailSender mailSender = mock(JavaMailSender.class);

    @Test
    void disabledEmailReturnsFailedDeliveryWithoutSending() {
        GmailEmailNotificationSender sender = new GmailEmailNotificationSender(
                mailSender,
                new GmailNotificationProperties(false, "sender@example.com"));

        NotificationDeliveryResult result = sender.send(new NotificationDeliveryRequest(
                NotificationChannel.EMAIL,
                "customer@example.com",
                "Subject",
                "Message",
                false));

        assertThat(result.delivered()).isFalse();
        assertThat(result.detail()).contains("Email delivery disabled");
    }
}
