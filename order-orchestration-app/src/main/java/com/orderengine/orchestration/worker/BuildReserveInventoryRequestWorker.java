package com.orderengine.orchestration.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.client.inventory.model.ReserveItemRequest;
import com.orderengine.client.inventory.model.ReserveRequest;
import com.orderengine.client.order.model.OrderResponse;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BuildReserveInventoryRequestWorker extends AbstractJobWorker{

    private final ObjectMapper objectMapper;

    BuildReserveInventoryRequestWorker(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @Override
    public String jobType(){
        return HandlerActivityName.BUILD_REVERSE_INVENTORY_REQUEST;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job){
        OrderResponse order = objectMapper.convertValue(
                job.getVariablesAsMap().get(ActivityParameters.ORDER),
                OrderResponse.class
        );

        ReserveRequest request = new ReserveRequest()
                .orderId(order.getOrderId())
                .items(order.getItems().stream()
                        .map(item -> new ReserveItemRequest()
                                .sku(item.getSku())
                                .quantity(item.getQuantity())
                        ).toList());

        return Map.of(ActivityParameters.RESERVE_REQUEST, request);
    }
}
