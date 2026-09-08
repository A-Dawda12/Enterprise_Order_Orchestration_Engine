package com.orderengine.client.order;

import com.orderengine.client.order.api.OrdersApi;
import com.orderengine.client.order.model.CreateOrderRequest;
import com.orderengine.client.order.model.InvoiceRequest;
import com.orderengine.client.order.model.InvoiceResponse;
import com.orderengine.client.order.model.OrderResponse;
import com.orderengine.client.order.model.OrderStatus;
import com.orderengine.client.order.model.PageOrderResponse;
import com.orderengine.client.order.model.StatusUpdateRequest;
import com.orderengine.client.order.model.ValidationResultResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderServiceClient {

    private final OrdersApi ordersApi;

    public OrderServiceClient(OrdersApi ordersApi) {
        this.ordersApi = ordersApi;
    }

    public OrderResponse getOrder(UUID orderId) {
        return ordersApi.getOrder(orderId);
    }

    public OrderResponse createOrder(UUID idempotencyKey, CreateOrderRequest request) {
        return ordersApi.createOrder(idempotencyKey, request);
    }

    public PageOrderResponse listOrders(OrderStatus status, Integer page, Integer size) {
        return ordersApi.listOrders(status, page, size);
    }

    public OrderResponse updateStatus(UUID orderId, UUID idempotencyKey ,StatusUpdateRequest request) {
        return ordersApi.updateStatus(orderId, idempotencyKey, request);
    }

    public InvoiceResponse generateInvoice(UUID orderId, UUID idempotencyKey, InvoiceRequest request) {
        return ordersApi.generateInvoice(orderId, idempotencyKey, request);
    }

    public ValidationResultResponse validateOrder(UUID orderId, UUID idempotencyKey) {
        return ordersApi.validateOrder(orderId, idempotencyKey);
    }
}
