package com.orderengine.order.client.orchestration;


import java.util.UUID;

public record StartWorkflowRequest(UUID orderId, String paymentMethodToken) {
}
