package com.orderengine.inventory.service.impl;

import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.OrderEngineException;
import com.orderengine.inventory.domain.InventoryEntity;
import com.orderengine.inventory.domain.ReservationEntity;
import com.orderengine.inventory.domain.ReservationItemEntity;
import com.orderengine.inventory.domain.ReservationStatus;
import com.orderengine.inventory.repository.InventoryRepository;
import com.orderengine.inventory.repository.ReservationRepository;
import com.orderengine.inventory.service.InventoryLockService;
import com.orderengine.inventory.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Duration RESERVATION_TTL = Duration.ofMinutes(15);

    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;
    private final InventoryLockService inventoryLockService;

    public InventoryServiceImpl(
            InventoryRepository inventoryRepository,
            ReservationRepository reservationRepository,
            InventoryLockService inventoryLockService
    ) {
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
        this.inventoryLockService = inventoryLockService;
    }

    @Override
    @Transactional
    public InventoryEntity getStock(String sku) {
        return inventoryRepository.findById(sku)
                .orElseThrow(() -> new OrderEngineException(ErrorCode.NOT_FOUND, "SKU not found: " + sku));
    }

    @Override
    @Transactional
    public ReservationEntity reserve(String orderId, List<ReserveItem> items) {
        if(orderId == null || orderId.isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "OrderId must not be blank");
        }
        if(items == null || items.isEmpty()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "Items must not be empty");
        }

        reservationRepository.findByOrderId(orderId).ifPresent(existing -> {
            throw new OrderEngineException(
                    ErrorCode.CONFLICT,
                    "Reservation already exists for orderId = " + orderId
            );
        });

        Set<String> skus = new LinkedHashSet<>();
        for(ReserveItem item : items) {
            if(item.sku() == null || item.sku().isBlank()) {
                throw new OrderEngineException(ErrorCode.BAD_REQUEST, "Item sku must not be blank");
            }
            if(item.quantity() <= 0) {
                throw new OrderEngineException(ErrorCode.BAD_REQUEST, "Item quantity must be greater than 0");
            }
            skus.add(item.sku());
        }

        List<String> locks = inventoryLockService.acquireAll(skus, orderId);
        try{
            for(ReserveItem item : items) {
                InventoryEntity stock = inventoryRepository.findBySkuForUpdate(item.sku())
                        .orElseThrow(() -> new OrderEngineException(
                                ErrorCode.NOT_FOUND,
                                "SKU not found: " + item.sku()
                        ));
                if (stock.getAvailableQuantity() < item.quantity()) {
                    throw new OrderEngineException(
                            ErrorCode.CONFLICT,
                            "Insufficient inventory for sku = " + item.sku()
                            + "; available=" + stock.getAvailableQuantity() + ", requested=" + item.quantity()
                    );
                }
                stock.setAvailableQuantity(stock.getAvailableQuantity() - item.quantity());
                stock.setReservedQuantity(stock.getReservedQuantity() + item.quantity());
                inventoryRepository.save(stock);
            }

            ReservationEntity reservation = new ReservationEntity();
            reservation.setReservationId(UUID.randomUUID().toString());
            reservation.setOrderId(orderId);
            reservation.setStatus(ReservationStatus.RESERVED);
            reservation.setExpiresAt(Instant.now().plus(RESERVATION_TTL));
            for(ReserveItem item : items) {
                ReservationItemEntity reservationItem = new ReservationItemEntity();
                reservationItem.setSku(item.sku());
                reservationItem.setQuantity(item.quantity());
                reservation.addItem(reservationItem);
            }
            return reservationRepository.save(reservation);
        } finally {
            inventoryLockService.releaseAll(locks);
        }
    }

    @Override
    @Transactional
    public void release(String reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new OrderEngineException(
                        ErrorCode.NOT_FOUND,
                        "Reservation not found" + reservationId
                ));

        if(reservation.getStatus() == ReservationStatus.RELEASED) {
            return;
        }
        if(reservation.getStatus() == ReservationStatus.CONFIRMED) {
            throw new OrderEngineException(
                    ErrorCode.CONFLICT,
                    "Cannot release a confirmed reservation: " + reservationId
            );
        }

        for(ReservationItemEntity item : reservation.getItems()) {
            InventoryEntity stock = inventoryRepository.findBySkuForUpdate(item.getSku())
                    .orElseThrow(() -> new OrderEngineException(
                            ErrorCode.NOT_FOUND,
                            "SKU not found: " + item.getSku()
                    ));
            stock.setAvailableQuantity(stock.getAvailableQuantity() + item.getQuantity());
            stock.setReservedQuantity(Math.max(0, stock.getReservedQuantity() - item.getQuantity()));
            inventoryRepository.save(stock);
        }
        reservation.setStatus(ReservationStatus.RELEASED);
        reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public ReservationEntity confirm(String reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new OrderEngineException(
                        ErrorCode.NOT_FOUND,
                        "Reservation not found: " + reservationId
                ));
        if(reservation.getStatus() == ReservationStatus.CONFIRMED) {
            return reservation;
        }
        if(reservation.getStatus() == ReservationStatus.RELEASED) {
            throw new OrderEngineException(
                    ErrorCode.CONFLICT,
                    "Only RESERVED reservations can be confirmed, " + reservationId + ", " + reservation.getStatus()
            );
        }

        for (ReservationItemEntity item : reservation.getItems()) {
            InventoryEntity stock = inventoryRepository.findBySkuForUpdate(item.getSku())
                    .orElseThrow(() -> new OrderEngineException(
                            ErrorCode.NOT_FOUND,
                            "SKU not found: " + item.getSku()
                    ));
            stock.setReservedQuantity(Math.max(0, stock.getReservedQuantity() - item.getQuantity()));
            inventoryRepository.save(stock);
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        return reservationRepository.save(reservation);
    }
}
