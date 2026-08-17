package com.orderengine.shipping.controller;

import com.orderengine.shipping.api.dto.CreateShipmentRequest;
import com.orderengine.shipping.api.dto.ShipmentResponse;
import com.orderengine.shipping.api.mapper.ShippingApiMapper;
import com.orderengine.shipping.domain.ShipmentEntity;
import com.orderengine.shipping.service.ShippingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/shipments")
public class ShippingController {

    private static final Logger log =
            LoggerFactory.getLogger(ShippingController.class);

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping
    public ResponseEntity<ShipmentResponse> create(
            @Valid @RequestBody CreateShipmentRequest request) {

        log.info(
                "createShipment orderId={} itemCount={}",
                request.orderId(),
                request.items().size()
        );

        ShipmentEntity created = shippingService.createShipment(
                request.orderId(),
                request.reservationId(),
                new ShippingService.ShippingAddress(
                        request.shippingAddress().line1(),
                        request.shippingAddress().city(),
                        request.shippingAddress().state(),
                        request.shippingAddress().postalCode(),
                        request.shippingAddress().country()
                ),
                request.items().stream()
                        .map(i -> new ShippingService.ShipmentItem(
                                i.sku(),
                                i.quantity()
                        ))
                        .toList()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ShippingApiMapper.toResponse(created));
    }

    @GetMapping("/{shipmentId}")
    public ShipmentResponse getShipment(
            @PathVariable String shipmentId) {

        log.info("getShipment shipmentId={}", shipmentId);

        return ShippingApiMapper.toResponse(
                shippingService.getShipment(shipmentId)
        );
    }

    @DeleteMapping("/{shipmentId}")
    public ResponseEntity<Void> cancel(
            @PathVariable String shipmentId) {

        log.info("cancelShipment shipmentId={}", shipmentId);

        shippingService.cancelShipment(shipmentId);

        return ResponseEntity.noContent().build();
    }
}