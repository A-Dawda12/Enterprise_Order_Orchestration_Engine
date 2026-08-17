package com.orderengine.fraud.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fraud_assessments")
public class FraudAssessmentEntity {

    @Id
    @Column(name = "assessment_id", length = 36, nullable = false)
    private String assessmentId;

    @Column(name = "order_id", length = 36, nullable = false)
    private String orderId;

    @Column(name = "score", nullable = false)
    private int score;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 16, nullable = false)
    private RiskLevel riskLevel;

    @Column(name = "requires_review", nullable = false)
    private boolean requiresReview;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "signals", columnDefinition = "jsonb")
    private List<String> signals = new ArrayList<>();

    @Column(name = "assessed_at", nullable = false)
    private Instant assessedAt;

    @PrePersist
    void onCreate() {
        assessedAt = Instant.now();

        if (signals == null) {
            signals = new ArrayList<>();
        }
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public boolean isRequiresReview() {
        return requiresReview;
    }

    public void setRequiresReview(boolean requiresReview) {
        this.requiresReview = requiresReview;
    }

    public List<String> getSignals() {
        return signals;
    }

    public void setSignals(List<String> signals) {
        this.signals = signals;
    }

    public Instant getAssessedAt() {
        return assessedAt;
    }
}