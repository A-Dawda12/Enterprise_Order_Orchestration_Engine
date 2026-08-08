package com.orderengine.order.repository;

import com.orderengine.order.domain.OrderEntity;
import com.orderengine.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    @EntityGraph(attributePaths = "items")
    @Override
    Optional<OrderEntity> findById(String id);

    @EntityGraph(attributePaths = "items")
    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);

    @EntityGraph(attributePaths = "items")
    @Override
    Page<OrderEntity> findAll(Pageable pageable);
}
