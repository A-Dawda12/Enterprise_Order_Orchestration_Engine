package com.orderengine.fraud.service;

import com.orderengine.fraud.domain.FraudAssessmentEntity;
import com.orderengine.fraud.domain.ReviewDecisionEntity;
import com.orderengine.fraud.domain.ReviewDecisionType;

import java.math.BigDecimal;

public interface FraudService {

    FraudAssessmentEntity assess(
            String orderId,
            String customerId,
            BigDecimal amount,
            String country,
            String postalCode,
            String paymentMethodToken
    );

    FraudAssessmentEntity getAssessment(String assessmentId);

    ReviewDecisionEntity recordDecision(
            String assessmentId,
            ReviewDecisionType decision,
            String reviewerId,
            String notes
    );
}
