package com.orderengine.payment.repository;

import com.orderengine.payment.domain.RefundEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<RefundEntity, String> {

    List<RefundEntity> findByPaymentId(String paymentId);

    Optional<RefundEntity> findFirstByPaymentIdOrderByRefundedAtDesc(String paymentId);
}
