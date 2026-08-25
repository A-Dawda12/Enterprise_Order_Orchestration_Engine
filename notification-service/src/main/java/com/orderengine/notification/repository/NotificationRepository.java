package com.orderengine.notification.repository;

import com.orderengine.notification.domain.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {

    List<NotificationEntity> findByOrderIdOrderByQueuedAtDesc(String orderId);
}
