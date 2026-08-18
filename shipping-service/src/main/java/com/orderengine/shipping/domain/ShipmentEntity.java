package com.orderengine.shipping.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "shipments")
public class ShipmentEntity {

    @Id
    @Column(name = "shipment_id", length = 36, nullable = false)
    private String shipmentId;

    @Column(name = "ordder_id", length = 36, nullable = false)
    private String orderId;

    @Column(name = "reservation_id", length = 36)
    private String reservationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32, nullable = false)
    private ShipmentStatus status;

    @Column(name = "carrier", length = 32)
    private String carrier;

    @Column(name = "tracking_number", length = 64)
    private String trackingNumber;

    @Column(name = "label_url", length = 512)
    private String labelUrl;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getLabelUrl() {
        return labelUrl;
    }

    public void setLabelUrl(String labelUrl) {
        this.labelUrl = labelUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
