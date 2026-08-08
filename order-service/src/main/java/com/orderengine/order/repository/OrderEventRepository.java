package com.orderengine.order.repository;

import com.orderengine.order.domain.OrderEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderEventRepository extends JpaRepository<OrderEventEntity, Long> {

    List<OrderEventEntity> findByOrderIdOrderByCreatedAtAsc(String orderId);
}
