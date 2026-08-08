package com.orderengine.inventory.repository;

import com.orderengine.inventory.domain.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<ReservationEntity, String> {

    Optional<ReservationEntity> findByOrderId(String orderId);
}
