package com.orderengine.payment.service.impl;

import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.OrderEngineException;
import com.orderengine.payment.domain.PaymentEntity;
import com.orderengine.payment.domain.PaymentStatus;
import com.orderengine.payment.domain.RefundEntity;
import com.orderengine.payment.domain.RefundStatus;
import com.orderengine.payment.gateway.PaymentGateway;
import com.orderengine.payment.repository.PaymentRepository;
import com.orderengine.payment.repository.RefundRepository;
import com.orderengine.payment.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final List<PaymentStatus> ACTIVE_STATUSES = List.of(PaymentStatus.AUTHORIZED, PaymentStatus.CAPTURED);

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final PaymentGateway paymentGateway;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            RefundRepository refundRepository,
            PaymentGateway paymentGateway
    ) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
        this.paymentGateway = paymentGateway;
    }

    @Override
    @Transactional
    public PaymentEntity authorize(
            String orderId,
            String customerId,
            BigDecimal amount,
            String currency,
            String paymentMethodTaken
    ) {
        if (orderId == null || orderId.isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "orderId must not be blank");
        }
        if (customerId == null || customerId.isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "customerId must not be blank");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "amount must be greater than zero");
        }
        if (currency == null || currency.isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "currency must be a 3-letter code");
        }
        if (paymentMethodTaken == null || paymentMethodTaken.isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "paymentMethodTaken must not be blank");
        }

        paymentRepository.findFirstByOrderIdAndStatusIn(orderId, ACTIVE_STATUSES).ifPresent(existing -> {
            throw new OrderEngineException(
                    ErrorCode.CONFLICT,
                    "Active payment already exists for orderId=" + orderId + " paymentId=" + existing.getPaymentId() + " with status=" + existing.getStatus()
            );
        });

        String gatewayRef = paymentGateway.authorize(orderId, amount, currency.toUpperCase(), paymentMethodTaken);

        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setOrderId(orderId);
        payment.setStatus(PaymentStatus.AUTHORIZED);
        payment.setAuthorizedAmount(amount);
        payment.setCapturedAmount(BigDecimal.ZERO);
        payment.setCurrency(currency.toUpperCase());
        payment.setGatewayRef(gatewayRef);
        payment.setAuthorizedAt(Instant.now());
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentEntity getPayment(String paymentId) {
        return findPayment(paymentId);
    }

    @Override
    @Transactional
    public PaymentEntity capture(String paymentId, BigDecimal amount) {
        PaymentEntity payment = findPayment(paymentId);

        if(payment.getStatus() == PaymentStatus.CAPTURED) {
            return payment;
        }

        if(payment.getStatus() != PaymentStatus.AUTHORIZED) {
            throw new OrderEngineException(
                    ErrorCode.CONFLICT,
                    "Payment must be AUTHORIZED to capture; status=" + payment.getStatus()
            );
        }
        BigDecimal capturedAmount = amount != null ? amount : payment.getAuthorizedAmount();
        if (capturedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "Capture amount must be greater than zero");
        }

        if (capturedAmount.compareTo(payment.getAuthorizedAmount()) > 0) {
            throw new OrderEngineException(
                    ErrorCode.BAD_REQUEST,
                    "Capture amount exceeds authorized amount; capturedAmount=" + capturedAmount + " authorizedAmount=" + payment.getAuthorizedAmount()
            );
        }

        paymentGateway.capture(payment.getGatewayRef(), capturedAmount);

        payment.setStatus(PaymentStatus.CAPTURED);
        payment.setCapturedAmount(capturedAmount);
        payment.setCapturedAt(Instant.now());
        return paymentRepository.save(payment);

    }

    @Override
    @Transactional
    public RefundEntity refund(String paymentId, String reason, BigDecimal amount) {
        PaymentEntity payment = findPayment(paymentId);

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            return refundRepository.findFirstByPaymentIdOrderByRefundedAtDesc(paymentId)
                    .orElseThrow(() -> new OrderEngineException(
                            ErrorCode.INTERNAL_ERROR,
                            "Payment is REFUNDED but no refund row found: " + paymentId));
        }

        if (payment.getStatus() != PaymentStatus.CAPTURED) {
            throw new OrderEngineException(
                    ErrorCode.CONFLICT,
                    "Payment must be captured before refunding; status=" + payment.getStatus()
                            + " (use void for AUTHORIZED)");
        }

        BigDecimal refundAmount = amount != null ? amount : payment.getCapturedAmount();
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "Refund amount must be greater than zero");
        }

        if (refundAmount.compareTo(payment.getCapturedAmount()) > 0) {
            throw new OrderEngineException(
                    ErrorCode.BAD_REQUEST,
                    "Refund amount exceeds captured amount; refundAmount=" + refundAmount + " capturedAmount=" + payment.getCapturedAmount()
            );
        }

        paymentGateway.refund(payment.getGatewayRef(), refundAmount);

        RefundEntity refund = new RefundEntity();
        refund.setRefundId(UUID.randomUUID().toString());
        refund.setPaymentId(paymentId);
        refund.setAmount(refundAmount);
        refund.setReason(reason);
        refund.setStatus(RefundStatus.REFUNDED);
        RefundEntity saved = refundRepository.save(refund);

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        return saved;
    }

    @Override
    @Transactional
    public PaymentEntity voidAuthorization(String paymentId) {
        PaymentEntity payment = findPayment(paymentId);

        if (payment.getStatus() != PaymentStatus.AUTHORIZED) {
            throw new OrderEngineException(
                    ErrorCode.CONFLICT,
                    "Payment must be AUTHORIZED to void; status=" + payment.getStatus()
            );
        }

        paymentGateway.voidAuthorization(payment.getGatewayRef());
        payment.setStatus(PaymentStatus.VOIDED);
        return paymentRepository.save(payment);
    }

    private PaymentEntity findPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new OrderEngineException(ErrorCode.NOT_FOUND, "Payment not found for ID: " + paymentId));
    }

}
