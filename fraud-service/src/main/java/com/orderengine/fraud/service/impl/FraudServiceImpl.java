package com.orderengine.fraud.service.impl;

import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.OrderEngineException;
import com.orderengine.fraud.api.dto.FraudAssessmentRequest;
import com.orderengine.fraud.domain.FraudAssessmentEntity;
import com.orderengine.fraud.domain.ReviewDecisionEntity;
import com.orderengine.fraud.domain.ReviewDecisionType;
import com.orderengine.fraud.repository.FraudAssessmentRepository;
import com.orderengine.fraud.repository.ReviewDecisionRepository;
import com.orderengine.fraud.service.FraudScoringEngine;
import com.orderengine.fraud.service.FraudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class FraudServiceImpl implements FraudService {

    private final FraudAssessmentRepository assessmentRepository;
    private final ReviewDecisionRepository decisionRepository;
    private final FraudScoringEngine scoringEngine;

    public FraudServiceImpl(
            FraudAssessmentRepository assessmentRepository,
            ReviewDecisionRepository decisionRepository,
            FraudScoringEngine scoringEngine
    ) {
        this.assessmentRepository = assessmentRepository;
        this.decisionRepository = decisionRepository;
        this.scoringEngine = scoringEngine;
    }

    @Override
    @Transactional
    public FraudAssessmentEntity assess(
            String orderId,
            String customerId,
            BigDecimal amount,
            String country,
            String postalCode,
            String paymentMethodToken
    ) {
        if(orderId == null || orderId.isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "orderId must not be blank");
        }
        if(customerId == null || customerId.isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "customerId must not be blank");
        }
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "amount must be greater than 0");
        }
        if(paymentMethodToken == null || paymentMethodToken.isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "paymentMethodToken must not be blank");
        }

        FraudScoringEngine.ScoreResult scored = scoringEngine.score(customerId, amount);

        FraudAssessmentEntity assessment = new FraudAssessmentEntity();
        assessment.setAssessmentId(UUID.randomUUID().toString());
        assessment.setOrderId(orderId);
        assessment.setScore(scored.score());
        assessment.setRiskLevel(scored.riskLevel());
        assessment.setRequiresReview(scored.requiresReview());
        assessment.setSignals(scored.signals());
        assessmentRepository.save(assessment);
        return assessment;
    }

    @Override
    @Transactional(readOnly = true)
    public FraudAssessmentEntity getAssessment(String assessmentId) {
        return findAssessment(assessmentId);
    }

    @Override
    @Transactional
    public ReviewDecisionEntity recordDecision(
            String assessmentId,
            ReviewDecisionType decision,
            String reviewerId,
            String notes
    ) {
        FraudAssessmentEntity assessment = findAssessment(assessmentId);

        if(!assessment.isRequiresReview()) {
            throw new OrderEngineException(ErrorCode.CONFLICT, "Assessment does not require review: " + assessmentId);
        }

        if(decision == null) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "decision must be APPROVED or REJECTED");
        }

        if(reviewerId == null || reviewerId.isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "reviewerId must not be blank");
        }

        ReviewDecisionEntity reviewDecision = new ReviewDecisionEntity();
        reviewDecision.setAssessmentId(assessmentId);
        reviewDecision.setDecision(decision);
        reviewDecision.setReviewerId(reviewerId);
        reviewDecision.setNotes(notes);
        decisionRepository.save(reviewDecision);

        return reviewDecision;
    }

    private FraudAssessmentEntity findAssessment(String assessmentId) {
        return assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new OrderEngineException(ErrorCode.NOT_FOUND, "Assessment not found: " + assessmentId));
    }
}
