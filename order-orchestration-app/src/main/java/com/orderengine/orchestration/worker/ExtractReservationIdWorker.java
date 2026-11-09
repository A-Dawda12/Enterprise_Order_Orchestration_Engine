package com.orderengine.orchestration.worker;

import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExtractReservationIdWorker extends AbstractJobWorker{

    @Override
    public String jobType() {
        return HandlerActivityName.EXTRACT_RESERVATION_ID;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        if(!(job.getVariablesAsMap().get(ActivityParameters.RESERVATION) instanceof Map<?,?> reservation)
                || reservation.get(ActivityParameters.RESERVATION_ID) == null
        ) {
            throw new IllegalArgumentException("reservation.reservtionId missing from process variables");
        }
        return Map.of(ActivityParameters.RESERVATION_ID, String.valueOf(reservation.get(ActivityParameters.RESERVATION_ID)));
    }
}
