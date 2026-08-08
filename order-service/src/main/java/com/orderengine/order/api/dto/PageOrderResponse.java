package com.orderengine.order.api.dto;

import java.util.List;

public record PageOrderResponse(
    List<OrderResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages
){
}
