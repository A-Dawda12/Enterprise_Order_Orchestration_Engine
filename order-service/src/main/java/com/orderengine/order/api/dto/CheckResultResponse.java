package com.orderengine.order.api.dto;

import java.util.List;

public record CheckResultResponse(
        boolean valid,
        List<String> errors
) {
}
