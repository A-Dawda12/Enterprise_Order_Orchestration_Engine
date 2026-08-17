package com.orderengine.fraud.controller;

import com.orderengine.fraud.api.dto.FraudAssessmentRequest;
import com.orderengine.fraud.api.dto.FraudAssessmentResponse;
import com.orderengine.fraud.api.dto.ReviewDecisionRequest;
import com.orderengine.fraud.api.mapper.FraudApiMapper;
import com.orderengine.fraud.domain.FraudAssessmentEntity;
import com.orderengine.fraud.service.FraudService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/assessments")
public class FraudController {

    private static final Logger log = LoggerFactory.getLogger(FraudController.class);

    private final FraudService fraudService;

    public FraudController(FraudService fraudService) {
        this.fraudService = fraudService;
    }

    @PostMapping
    public ResponseEntity<FraudAssessmentResponse> assess(
            @Valid @RequestBody FraudAssessmentRequest request) {

        log.info("assess orderId={} customerId={} amount={}",
                request.orderId(),
                request.customerId(),
                request.amount());

        FraudAssessmentEntity created = fraudService.assess(
                request.orderId(),
                request.customerId(),
                request.amount(),
                request.shippingAddress().country(),
                request.shippingAddress().postalCode(),
                request.paymentMethodToken()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(FraudApiMapper.toResponse(created));
    }

    @GetMapping("/{assessmentId}")
    public FraudAssessmentResponse getAssessment(
            @PathVariable String assessmentId) {

        log.info("getAssessment assessmentId={}", assessmentId);

        return FraudApiMapper.toResponse(
                fraudService.getAssessment(assessmentId)
        );
    }

    @PostMapping("/{assessmentId}/decision")
    public FraudAssessmentResponse recordDecision(
            @PathVariable String assessmentId,
            @Valid @RequestBody ReviewDecisionRequest request) {

        log.info("decision assessmentId={} decision={} reviewerId={}",
                assessmentId,
                request.decision(),
                request.reviewerId());

        fraudService.recordDecision(
                assessmentId,
                request.decision(),
                request.reviewerId(),
                request.notes()
        );

        return FraudApiMapper.toResponse(
                fraudService.getAssessment(assessmentId)
        );
    }
}