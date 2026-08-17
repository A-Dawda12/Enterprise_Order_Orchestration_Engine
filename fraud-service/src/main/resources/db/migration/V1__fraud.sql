-- Fraud Service schema (LLD §5.4)

CREATE TABLE fraud_assessments (
       assessment_id   VARCHAR(36) PRIMARY KEY,
       order_id        VARCHAR(36) NOT NULL,
       score           INT NOT NULL CHECK (score BETWEEN 0 AND 100),
       risk_level      VARCHAR(16) NOT NULL,
       requires_review BOOLEAN NOT NULL DEFAULT FALSE,
       signals         JSONB,
       assessed_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fraud_assessments_order_id
    ON fraud_assessments (order_id);

CREATE TABLE review_decisions (
      id              BIGSERIAL PRIMARY KEY,
      assessment_id   VARCHAR(36) NOT NULL REFERENCES fraud_assessments (assessment_id),
      decision        VARCHAR(16) NOT NULL,
      reviewer_id     VARCHAR(36) NOT NULL,
      notes           TEXT,
      decided_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_review_decisions_assessment_id
    ON review_decisions (assessment_id);