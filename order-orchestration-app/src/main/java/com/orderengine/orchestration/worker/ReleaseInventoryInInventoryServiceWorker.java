package com.orderengine.orchestration.worker;

import com.orderengine.client.inventory.InventoryServiceClient;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ReleaseInventoryInInventoryServiceWorker extends AbstractJobWorker{

    private final InventoryServiceClient inventoryServiceClient;

    ReleaseInventoryInInventoryServiceWorker(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.RELEASE_INVENTORY_IN_INVENTORY_SERVICE;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        Object reservationId = job.getVariablesAsMap().get(ActivityParameters.RESERVATION_ID);
        if(reservationId == null) {
            log.info("No reservationId - nothing to release");
            return Map.of();
        }
        inventoryServiceClient.release((String.valueOf(reservationId)));
        return Map.of();
    }
}
