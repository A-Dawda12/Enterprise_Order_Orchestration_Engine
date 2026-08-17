package com.orderengine.fraud.mapper;

import com.orderengine.fraud.api.dto.FraudAssessmentResponse;
import com.orderengine.fraud.domain.FraudAssessmentEntity;

public final class FraudApiMapper {

    private FraudApiMapper() {

    }

    public static FraudAssessmentResponse toResponse(FraudAssessmentEntity entity) {
        return new FraudAssessmentResponse(
                entity.getAssessmentId(),
                entity.getOrderId(),
                entity.getScore(),
                entity.getRiskLevel().name(),
                entity.isRequiresReview(),
                entity.getSignals(),
                entity.getAssessedAt()
        );
    }
}
