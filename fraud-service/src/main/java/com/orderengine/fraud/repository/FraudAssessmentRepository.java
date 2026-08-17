package com.orderengine.fraud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FraudAssessmentRepository extends JpaRepository<FraudAssessmentRepository, String> {

    Optional<FraudAssessmentRepository> findFirstByOrderIdOrderByAssessmentAtDesc(String orderId);
}
