package com.orderengine.payment.controller;

import com.orderengine.payment.api.dto.*;
import com.orderengine.payment.domain.PaymentEntity;
import com.orderengine.payment.domain.RefundEntity;
import com.orderengine.payment.mapper.PaymentApiMapper;
import com.orderengine.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/authorize")
    public ResponseEntity<PaymentResponse> authorize(@Valid @RequestBody AuthorizeRequest request) {
        log.info("authorize orderId={} amount={}", request.orderId(), request.amount());
        PaymentEntity created = paymentService.authorize(
                request.orderId(),
                request.customerId(),
                request.amount(),
                request.currency(),
                request.paymentMethodToken()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentApiMapper.toPaymentResponse(created));
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(@PathVariable String paymentId) {
        log.info("getPayment paymentId={}", paymentId);
        return PaymentApiMapper.toPaymentResponse(paymentService.getPayment(paymentId));
    }

    @PostMapping("/{paymentId}/capture")
    public PaymentResponse capture(
            @PathVariable String paymentId,
            @RequestBody(required = false) @Valid CaptureRequest request
    ) {
        log.info("capture paymentId={}", paymentId);

        return PaymentApiMapper.toPaymentResponse(
                paymentService.capture(
                        paymentId,
                        request != null ? request.amount() : null
                )
        );
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<RefundResponse> refund(
            @PathVariable String paymentId,
            @Valid @RequestBody RefundRequest request
    ) {
        log.info(
                "refund paymentId={} reason={}",
                paymentId,
                request.reason()
        );

        RefundEntity refund = paymentService.refund(
                paymentId,
                request.reason(),
                request.amount()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PaymentApiMapper.toRefundResponse(refund));
    }

    @PostMapping("/{paymentId}/void")
    public PaymentResponse voidAuthorization(
            @PathVariable String paymentId
    ) {
        log.info("void paymentId={}", paymentId);

        return PaymentApiMapper.toPaymentResponse(
                paymentService.voidAuthorization(paymentId)
        );
    }
}
