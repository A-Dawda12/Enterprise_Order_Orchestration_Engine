package com.orderengine.orchestration.worker;

import com.orderengine.client.notification.NotificationServiceClient;
import com.orderengine.client.notification.model.NotificationResponse;
import com.orderengine.client.notification.model.SendNotificationRequest;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class SendCancelledNotificationWorker extends AbstractJobWorker {

    private final NotificationServiceClient notificationServiceClient;
    private final ObjectMapper objectMapper;

    public SendCancelledNotificationWorker(NotificationServiceClient notificationServiceClient, ObjectMapper objectMapper) {
        this.notificationServiceClient = notificationServiceClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.SEND_ORDER_CANCELLED_NOTIFICATION;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        SendNotificationRequest request = objectMapper.convertValue(
                job.getVariablesAsMap().get(ActivityParameters.NOTIFICATION_REQUEST),
                SendNotificationRequest.class
        );

        NotificationResponse notificationResponse = notificationServiceClient.send(
                idempotencyKey(job).toString(),
                request
        );

        return Map.of(ActivityParameters.NOTIFICATION, notificationResponse);
    }

}
