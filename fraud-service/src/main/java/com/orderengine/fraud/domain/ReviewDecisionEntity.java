package com.orderengine.fraud.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "review_decisions")
public class ReviewDecisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "assessment_id", length = 36, nullable = false)
    private String assessmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", length = 16, nullable = false)
    private ReviewDecisionType decision;

    @Column(name = "reviewer_id", length = 36, nullable = false)
    private String reviewerId;

    @Column(name = "notes")
    private String notes;

    @Column(name = "decided_at", nullable = false)
    private Instant decidedAt;

    @PrePersist
    void onCreate() {
        decidedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public ReviewDecisionType getDecision() {
        return decision;
    }

    public void setDecision(ReviewDecisionType decision) {
        this.decision = decision;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }
}