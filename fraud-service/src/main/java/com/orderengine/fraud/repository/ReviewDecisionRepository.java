package com.orderengine.fraud.repository;

import com.orderengine.fraud.domain.ReviewDecisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewDecisionRepository extends JpaRepository<ReviewDecisionEntity, Long> {

    List<ReviewDecisionEntity> findByAssessmentIdOrderByDecidedAtDesc(String assessmentId);

    Optional<ReviewDecisionEntity> findFirstByAssessmentIdOrderByDecidedAtDesc(String assessmentId);
}
