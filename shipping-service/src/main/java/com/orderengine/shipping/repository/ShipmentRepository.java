package com.orderengine.shipping.repository;

import com.orderengine.shipping.domain.ShipmentEntity;
import com.orderengine.shipping.domain.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<ShipmentEntity, String> {

    Optional<ShipmentEntity> findFirstByOrderIdAndStatus(String orderId, ShipmentStatus status);
}
