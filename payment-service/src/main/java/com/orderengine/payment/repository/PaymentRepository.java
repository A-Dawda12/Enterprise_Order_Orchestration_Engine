package com.orderengine.payment.repository;

import com.orderengine.payment.domain.PaymentEntity;
import com.orderengine.payment.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {

    List<PaymentEntity> findByOrderId(String orderId);

    Optional<PaymentEntity> findFirstByOrderIdAndStatusIn(String orderId, List<PaymentStatus> statuses);
}
