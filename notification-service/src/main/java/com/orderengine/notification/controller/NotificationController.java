package com.orderengine.notification.controller;

import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.OrderEngineException;
import com.orderengine.notification.api.dto.NotificationResponse;
import com.orderengine.notification.api.dto.SendNotificationRequest;
import com.orderengine.notification.api.mapper.NotificationApiMapper;
import com.orderengine.notification.domain.NotificationChannel;
import com.orderengine.notification.domain.NotificationEntity;
import com.orderengine.notification.domain.NotificationTemplate;
import com.orderengine.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/notifications")
public class NotificationController {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> send(
            @Valid @RequestBody SendNotificationRequest request) {

        log.info(
                "sendNotification orderId={} customerId={} template={}",
                request.orderId(),
                request.customerId(),
                request.template()
        );

        NotificationEntity created = notificationService.send(
                request.orderId(),
                request.customerId(),
                parseChannel(request.channel()),
                parseTemplate(request.template()),
                request.variables()
        );

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(NotificationApiMapper.toResponse(created));
    }

    @GetMapping("/{notificationId}")
    public NotificationResponse getNotification(
            @PathVariable String notificationId) {

        log.info(
                "getNotification notificationId={}",
                notificationId
        );

        return NotificationApiMapper.toResponse(
                notificationService.getNotification(notificationId)
        );
    }

    private static NotificationChannel parseChannel(String raw) {
        try {
            NotificationChannel channel =
                    NotificationChannel.valueOf(raw.trim().toUpperCase());

            if (channel != NotificationChannel.EMAIL) {
                throw new OrderEngineException(
                        ErrorCode.BAD_REQUEST,
                        "channel must be EMAIL"
                );
            }

            return channel;

        } catch (OrderEngineException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new OrderEngineException(
                    ErrorCode.BAD_REQUEST,
                    "channel must be EMAIL"
            );
        }
    }

    private static NotificationTemplate parseTemplate(String raw) {
        try {
            return NotificationTemplate.valueOf(
                    raw.trim().toUpperCase()
            );

        } catch (Exception ex) {
            throw new OrderEngineException(
                    ErrorCode.BAD_REQUEST,
                    "template must be ORDER_SHIPPED or ORDER_CANCELLED"
            );
        }
    }
}