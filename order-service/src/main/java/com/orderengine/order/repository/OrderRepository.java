package com.orderengine.order.repository;

import com.orderengine.order.domain.OrderEntity;
import com.orderengine.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);
}
