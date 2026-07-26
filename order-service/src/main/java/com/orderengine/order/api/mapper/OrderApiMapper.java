package com.orderengine.order.api.mapper;

import com.orderengine.order.api.dto.CreateOrderRequest;
import com.orderengine.order.api.dto.OrderResponse;
import com.orderengine.order.api.dto.PageOrderResponse;
import com.orderengine.order.domain.OrderEntity;
import com.orderengine.order.domain.OrderItemEntity;
import com.orderengine.order.domain.ShippingAddress;
import com.orderengine.order.service.OrderService;
import org.springframework.data.domain.Page;

import java.util.List;

public final class OrderApiMapper {

    private OrderApiMapper() {

    }

    public static List<OrderService.NewOrderItem> toNewItems(CreateOrderRequest request) {
        return request.items().stream()
                .map(item -> new OrderService.NewOrderItem(item.sku(), item.quantity(), item.unitPrice()))
                .toList();
    }

    public static ShippingAddress toShippingAddress(CreateOrderRequest.ShippingAddressRequest address) {
        return new ShippingAddress(
                address.line1(),
                address.line2(),
                address.city(),
                address.state(),
                address.postalCode(),
                address.country()
        );
    }

    public static OrderResponse toResponse(OrderEntity order) {
        List<OrderResponse.OrderItemResponse> items = order.getItems().stream()
                .map(OrderApiMapper::toItemResponse)
                .toList();

        ShippingAddress address = order.getShippingAddress();
        OrderResponse.ShippingAddressResponse addressResponse = address == null ? null
                : new OrderResponse.ShippingAddressResponse(
                address.line1(),
                address.line2(),
                address.city(),
                address.state(),
                address.postalCode(),
                address.country()
        );

        return new OrderResponse(
                order.getOrderId(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getWorkflowKey(),
                items,
                addressResponse,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public static PageOrderResponse toPageResponse(Page<OrderEntity> page) {
        List<OrderResponse> content = page.getContent().stream()
                .map(OrderApiMapper::toResponse)
                .toList();

        return new PageOrderResponse(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private static OrderResponse.OrderItemResponse toItemResponse(OrderItemEntity item) {
        return new OrderResponse.OrderItemResponse(item.getSku(), item.getQuantity(), item.getUnitPrice());
    }
}
