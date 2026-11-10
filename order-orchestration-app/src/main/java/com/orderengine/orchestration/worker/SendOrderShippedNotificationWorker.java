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
public class SendOrderShippedNotificationWorker extends AbstractJobWorker{


    private final ObjectMapper objectMapper;
    private final NotificationServiceClient notificationServiceClient;

    public SendOrderShippedNotificationWorker(ObjectMapper objectMapper, NotificationServiceClient notificationServiceClient) {
        this.objectMapper = objectMapper;
        this.notificationServiceClient = notificationServiceClient;
    }


    @Override
    public String jobType() {
        return HandlerActivityName.SEND_ORDER_SHIPPED_NOTIFICATION;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        SendNotificationRequest request = objectMapper.convertValue(
                job.getVariablesAsMap().get(ActivityParameters.NOTIFICATION_REQUEST),
                SendNotificationRequest.class
        );

        NotificationResponse notification = notificationServiceClient.send(
                idempotencyKey(job).toString(), request
        );
        return Map.of(ActivityParameters.NOTIFICATION, notification);
    }
}
