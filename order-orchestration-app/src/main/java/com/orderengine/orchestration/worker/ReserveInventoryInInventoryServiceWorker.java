package com.orderengine.orchestration.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.client.inventory.InventoryServiceClient;
import com.orderengine.client.inventory.model.ReservationResponse;
import com.orderengine.client.inventory.model.ReserveRequest;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReserveInventoryInInventoryServiceWorker extends AbstractJobWorker{

    private final InventoryServiceClient inventoryServiceClient;
    private final ObjectMapper objectMapper;

    public ReserveInventoryInInventoryServiceWorker(InventoryServiceClient inventoryServiceClient, ObjectMapper objectMapper) {
        this.inventoryServiceClient = inventoryServiceClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.RESERVE_INVENTORY_IN_INVENTORY_SERVICE;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        ReserveRequest request = objectMapper.convertValue(
                job.getVariablesAsMap().get(ActivityParameters.RESERVE_REQUEST),
                ReserveRequest.class
        );
        ReservationResponse reservation = inventoryServiceClient.reserve(
                idempotencyKey(job).toString(), request
        );

        return Map.of(ActivityParameters.RESERVATION, reservation);
    }
}
