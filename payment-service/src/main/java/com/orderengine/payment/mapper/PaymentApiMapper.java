package com.orderengine.payment.mapper;

import com.orderengine.payment.api.dto.PaymentResponse;
import com.orderengine.payment.api.dto.RefundResponse;
import com.orderengine.payment.domain.PaymentEntity;
import com.orderengine.payment.domain.RefundEntity;

public final class PaymentApiMapper {

    private PaymentApiMapper() {

    }

    public static PaymentResponse toPaymentResponse(PaymentEntity entity) {
        return new PaymentResponse(
                entity.getPaymentId(),
                entity.getOrderId(),
                entity.getStatus().name(),
                entity.getAuthorizedAmount(),
                entity.getCapturedAmount(),
                entity.getCurrency(),
                entity.getAuthorizedAt()
        );
    }

    public static RefundResponse toRefundResponse(RefundEntity entity) {
        return new RefundResponse(
                entity.getRefundId(),
                entity.getPaymentId(),
                entity.getStatus().name(),
                entity.getAmount(),
                entity.getRefundedAt()
        );
    }
}
