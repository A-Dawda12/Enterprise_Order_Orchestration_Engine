package com.orderengine.inventory.controller;

import com.orderengine.inventory.api.dto.ReservationResponse;
import com.orderengine.inventory.api.dto.ReserveRequest;
import com.orderengine.inventory.api.dto.StockResponse;
import com.orderengine.inventory.domain.ReservationEntity;
import com.orderengine.inventory.mapper.InventoryApiMapper;
import com.orderengine.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/inventory/{sku}")
    public StockResponse getStock(@PathVariable String sku) {
        return InventoryApiMapper.toStockResponse(inventoryService.getStock(sku));
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> reserve(@Valid @RequestBody ReserveRequest request) {
        log.info("reserve orderId={} itemCount={}", request.orderId(), request.items().size());
        ReservationEntity created = inventoryService.reserve(
                request.orderId(),
                request.items().stream()
                        .map(i -> new InventoryService.ReserveItem(i.sku(), i.quantity()))
                        .toList()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InventoryApiMapper.toReservationResponse(created));
    }

    @DeleteMapping("/reservation/{reservationId}")
    public ResponseEntity<Void> release(@PathVariable String reservationId) {
        log.info("release reservationId={}", reservationId);
        inventoryService.release(reservationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reservation/{reservationId}/confirm")
    public ReservationResponse confirm(@PathVariable String reservationId) {
        log.info("confirm reservationId={}", reservationId);
        ReservationEntity confirmed = inventoryService.confirm(reservationId);
        return InventoryApiMapper.toReservationResponse(confirmed);
    }
}
