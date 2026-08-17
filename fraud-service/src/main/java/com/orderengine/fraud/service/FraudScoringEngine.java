package com.orderengine.fraud.service;

import com.orderengine.fraud.config.FraudProperties;
import com.orderengine.fraud.domain.RiskLevel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class FraudScoringEngine {

    public static final String SIGNAL_NEW_ACCOUNT = "NEW_ACCOUNT";
    public static final String SIGNAL_HIGH_VALUE_ORDER = "HIGH_VALUE_ORDER";

    private final FraudProperties properties;

    public FraudScoringEngine(FraudProperties properties) {
        this.properties = properties;
    }

    public ScoreResult score(String customerId, BigDecimal amount) {
        List<String> signals = new ArrayList<>();
        int score = 0;

        if(isNewAccount(customerId)) {
            signals.add(SIGNAL_NEW_ACCOUNT);
            score += properties.getNewAccountPoints();
        }
        if(isHighValue(amount)) {
            signals.add(SIGNAL_HIGH_VALUE_ORDER);
            score += properties.getHighValuePoints();
        }

        score = Math.min(100, Math.max(0, score));
        boolean requiresReview = score > properties.getReviewScoreThreshold();
        return new ScoreResult(score, toRisLevel(score), requiresReview, List.copyOf(signals));
    }

    public boolean isNewAccount(String customerId) {
        if(customerId == null || customerId.isBlank()) {
            return false;
        }

        String normalized = customerId.toLowerCase(Locale.ROOT);
        for(String prefix : properties.getNewAccountPrefixes()) {
            if(prefix != null && !prefix.isBlank() && normalized.startsWith(prefix.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private boolean isHighValue(BigDecimal amount) {
        return amount != null && amount.compareTo(properties.getHighValueThreshold()) >= 0;
    }

    private static RiskLevel toRisLevel(int score) {
        if(score > 80) {
            return RiskLevel.HIGH;
        }
        if(score >= 40) {
            return RiskLevel.MEDIUM;
        }
        return RiskLevel.LOW;
    }

    public record ScoreResult(
            int score,
            RiskLevel riskLevel,
            boolean requiresReview,
            List<String> signals
    ){
    }
}
