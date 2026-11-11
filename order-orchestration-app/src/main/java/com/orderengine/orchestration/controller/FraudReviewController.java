package com.orderengine.orchestration.controller;

import com.orderengine.orchestration.api.dto.CompleteFraudReviewRequest;
import com.orderengine.orchestration.service.FraudReviewCompletionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class FraudReviewController {

    private final FraudReviewCompletionService fraudReviewCompletionService;

    public FraudReviewController(FraudReviewCompletionService fraudReviewCompletionService) {
        this.fraudReviewCompletionService = fraudReviewCompletionService;
    }

    @PostMapping("/fraud-reviews/complete")
    ResponseEntity<Void> complete(@Valid @RequestBody CompleteFraudReviewRequest request) {
        fraudReviewCompletionService.complete(
                request.orderId(),
                request.decision(),
                request.reviewerId(),
                request.notes()
        );
        return ResponseEntity.noContent().build();
    }
}
