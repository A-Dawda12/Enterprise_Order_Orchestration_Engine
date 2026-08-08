package com.orderengine.order.repository;

import com.orderengine.order.domain.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, String> {

    List<InvoiceEntity> findByOrderId(String orderId);

    Optional<InvoiceEntity> findFirstByOrderIdOrderByIssuedAtDesc(String orderId);
}
