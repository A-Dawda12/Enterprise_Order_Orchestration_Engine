package com.orderengine.fraud.repository;

import com.orderengine.fraud.domain.FraudAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FraudAssessmentRepository extends JpaRepository<FraudAssessmentEntity, String> {

    Optional<FraudAssessmentEntity> findFirstByOrderIdOrderByAssessedAtDesc(String orderId);
}
