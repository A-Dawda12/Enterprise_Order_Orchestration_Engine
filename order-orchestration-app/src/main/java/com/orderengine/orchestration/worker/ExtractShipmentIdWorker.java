package com.orderengine.orchestration.worker;

import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExtractShipmentIdWorker extends AbstractJobWorker{

    @Override
    public String jobType(){
        return HandlerActivityName.EXTRACT_SHIPMENT_ID;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job){
        if(!(job.getVariablesAsMap().get(ActivityParameters.SHIPMENT) instanceof Map<?,?> shipment)
        || shipment.get(ActivityParameters.SHIPMENT_ID) == null){
            throw new IllegalStateException("shipment.shipmentId missing from process variables");
        }
        return Map.of(ActivityParameters.SHIPMENT_ID, String.valueOf(shipment.get(ActivityParameters.SHIPMENT_ID)));
    }
}
