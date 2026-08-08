package com.orderengine.order.domain;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class OrderStatusTransitions {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = new EnumMap<>(OrderStatus.class);

    static {
        ALLOWED.put(OrderStatus.CREATED, EnumSet.of(
                OrderStatus.VALIDATED,
                OrderStatus.REJECTED,
                OrderStatus.CANCELLED
        ));
        ALLOWED.put(OrderStatus.VALIDATED, EnumSet.of(
                OrderStatus.PROCESSING,
                OrderStatus.REJECTED,
                OrderStatus.CANCELLED
        ));
        ALLOWED.put(OrderStatus.PROCESSING, EnumSet.of(
                OrderStatus.PAYMENT_PENDING,
                OrderStatus.COMPENSATING,
                OrderStatus.CANCELLED
        ));
        ALLOWED.put(OrderStatus.PAYMENT_PENDING, EnumSet.of(
                OrderStatus.FRAUD_REVIEW,
                OrderStatus.PAID,
                OrderStatus.PAYMENT_TIMEOUT,
                OrderStatus.COMPENSATING
        ));
        ALLOWED.put(OrderStatus.FRAUD_REVIEW, EnumSet.of(
                OrderStatus.PAID,
                OrderStatus.REJECTED,
                OrderStatus.COMPENSATING,
                OrderStatus.CANCELLED
        ));
        ALLOWED.put(OrderStatus.PAID, EnumSet.of(
                OrderStatus.SHIPPED,
                OrderStatus.COMPENSATING
        ));
        ALLOWED.put(OrderStatus.SHIPPED, EnumSet.of(
                OrderStatus.COMPLETED,
                OrderStatus.COMPENSATING
        ));
        ALLOWED.put(OrderStatus.PAYMENT_TIMEOUT, EnumSet.of(
                OrderStatus.COMPENSATING
        ));
        ALLOWED.put(OrderStatus.COMPENSATING, EnumSet.of(
                OrderStatus.CANCELLED,
                OrderStatus.FAILED
        ));
        ALLOWED.put(OrderStatus.COMPLETED, EnumSet.noneOf(
                OrderStatus.class
        ));
        ALLOWED.put(OrderStatus.CANCELLED, EnumSet.noneOf(
                OrderStatus.class
        ));
        ALLOWED.put(OrderStatus.FAILED, EnumSet.noneOf(
                OrderStatus.class
        ));
        ALLOWED.put(OrderStatus.REJECTED, EnumSet.noneOf(
                OrderStatus.class
        ));
    }

    private OrderStatusTransitions() {
    }

    public static boolean isAllowed(OrderStatus from, OrderStatus to) {
        if(from == null || to == null) {
            return false;
        }

        if(from == to) {
            return true;
        }

        Set<OrderStatus> next = ALLOWED.get(from);
        return next != null && next.contains(to);
    }

    public static Set<OrderStatus> allowedFrom(OrderStatus from) {
        Set<OrderStatus> next = ALLOWED.get(from);
        return next == null ? EnumSet.noneOf(OrderStatus.class) : EnumSet.copyOf(next);
    }
}
