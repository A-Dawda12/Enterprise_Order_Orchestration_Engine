package com.orderengine.notification.service.impl;

import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.OrderEngineException;
import com.orderengine.notification.domain.NotificationChannel;
import com.orderengine.notification.domain.NotificationEntity;
import com.orderengine.notification.domain.NotificationStatus;
import com.orderengine.notification.domain.NotificationTemplate;
import com.orderengine.notification.gateway.NotificationProvider;
import com.orderengine.notification.repository.NotificationRepository;
import com.orderengine.notification.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationProvider notificationProvider;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            NotificationProvider notificationProvider
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationProvider = notificationProvider;
    }

    @Override
    @Transactional
    public NotificationEntity send(
            String orderId,
            String customerId,
            NotificationChannel channel,
            NotificationTemplate template,
            Map<String, String> variables
    ) {
        if (orderId == null || orderId.isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "orderId must not be blank");
        }
        if (customerId == null || customerId.isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "customerId must not be blank");
        }
        if (channel == null) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "channel must be EMAIL");
        }
        if (template == null) {
            throw new OrderEngineException(
                    ErrorCode.BAD_REQUEST,
                    "template must be ORDER_SHIPPED or ORDER_CANCELLED"
            );
        }

        String recipient = customerId + "@example.com";
        Map<String, String> vars = variables != null ? new HashMap<>(variables) : new HashMap<>();

        NotificationEntity notification = new NotificationEntity();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setOrderId(orderId);
        notification.setCustomerId(customerId);
        notification.setChannel(channel);
        notification.setTemplate(template);
        notification.setStatus(NotificationStatus.QUEUED);
        notification.setRecipient(recipient);
        notification.setVariables(vars);

        NotificationEntity saved = notificationRepository.save(notification);
        notificationProvider.send(
                saved.getNotificationId(),
                recipient,
                template.name(),
                vars
        );
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationEntity getNotification(String notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new OrderEngineException(
                        ErrorCode.NOT_FOUND,
                        "Notification not found: " + notificationId
                ));
    }
}
