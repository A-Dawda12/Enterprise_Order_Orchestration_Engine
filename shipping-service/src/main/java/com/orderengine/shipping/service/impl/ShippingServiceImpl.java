package com.orderengine.shipping.service.impl;

import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.OrderEngineException;
import com.orderengine.shipping.domain.ShipmentEntity;
import com.orderengine.shipping.domain.ShipmentStatus;
import com.orderengine.shipping.gateway.CarrierGateway;
import com.orderengine.shipping.repository.ShipmentRepository;
import com.orderengine.shipping.service.ShippingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ShippingServiceImpl implements ShippingService {

    private final ShipmentRepository shipmentRepository;
    private final CarrierGateway carrierGateway;

    public ShippingServiceImpl(ShipmentRepository shipmentRepository, CarrierGateway carrierGateway) {
        this.shipmentRepository = shipmentRepository;
        this.carrierGateway = carrierGateway;
    }

    @Override
    @Transactional
    public ShipmentEntity createShipment(
            String orderId,
            String reservationId,
            ShippingAddress address,
            List<ShipmentItem> items
    ) {
        if(orderId == null || orderId.isEmpty()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "orderId must not be blank");
        }
        if(address == null) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "address must not be null");
        }
        if(address.line1() == null || address.line1().isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "address.line1 must not be blank");
        }
        if(address.city() == null || address.city().isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "address.city must not be blank");
        }
        if(address.postalCode() == null || address.postalCode().isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "address.postalCode must not be blank");
        }
        if(address.country() == null || address.country().isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "address.country must not be blank");
        }
        if(items == null || items.isEmpty()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "items must not be null or empty");
        }
        for(ShipmentItem item : items) {
            if(item.sku() == null || item.sku().isBlank()) {
                throw new OrderEngineException(ErrorCode.BAD_REQUEST, "item.sku must not be blank");
            }
            if(item.quantity() <= 0) {
                throw new OrderEngineException(ErrorCode.BAD_REQUEST, "item.quantity must be greater than 0");
            }
        }

        shipmentRepository.findFirstByOrderIdAndStatus(orderId, ShipmentStatus.LABEL_CREATED).ifPresent(existing ->{
            throw new OrderEngineException(
                    ErrorCode.CONFLICT,
                    "Active shipmennt already exists for orderId=" + orderId + " shipmentId=" + existing.getShipmentId()
            );
        });

        String shipmentId = UUID.randomUUID().toString();
        CarrierGateway.LabelResult label = carrierGateway.createLabel(
                shipmentId,
                orderId,
                address.postalCode(),
                address.country()
        );

        ShipmentEntity shipment = new ShipmentEntity();
        shipment.setShipmentId(shipmentId);
        shipment.setOrderId(orderId);
        shipment.setReservationId(reservationId);
        shipment.setStatus(ShipmentStatus.LABEL_CREATED);
        shipment.setCarrier(label.carrier());
        shipment.setTrackingNumber(label.trackingNumber());
        shipment.setLabelUrl(label.labelUrl());
        shipmentRepository.save(shipment);
        return shipment;
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentEntity getShipment(String shipmentId) {
        return findShipment(shipmentId);
    }

    @Override
    @Transactional
    public void cancelShipment(String shipmentId) {
        ShipmentEntity shipment = findShipment(shipmentId);
        if(shipment.getStatus() == ShipmentStatus.CANCELLED) {
            return;
        }

        if(shipment.getStatus() != ShipmentStatus.LABEL_CREATED) {
            throw new OrderEngineException(
                    ErrorCode.BAD_REQUEST,
                    "Only LABEL_CREATED shipments can be cancelled; status: " + shipment.getStatus()
            );
        }

        carrierGateway.cancelLabel(shipment.getTrackingNumber());
        shipment.setStatus(ShipmentStatus.CANCELLED);
        shipment.setCancelledAt(Instant.now());
        shipmentRepository.save(shipment);
    }

    private ShipmentEntity findShipment(String shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new OrderEngineException(
                        ErrorCode.NOT_FOUND,
                        "Shipment not found: " + shipmentId
                ));
    }
}
