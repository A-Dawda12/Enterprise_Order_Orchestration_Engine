package com.orderengine.order.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "invoices")
public class InvoiceEntity {

    @Id
    @Column(name = "invoice_id", length = 36, nullable = false)
    private String invoiceId;

    @Column(name = "order_id", length = 36, nullable = false)
    private String orderId;

    @Column(name = "payment_id", length = 36, nullable = false)
    private String paymentId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "pdf_url", length = 512)
    private String pdfUrl;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @PrePersist
    void onCreate() {
        issuedAt = Instant.now();
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

}
