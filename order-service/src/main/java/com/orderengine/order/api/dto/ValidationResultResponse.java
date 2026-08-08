package com.orderengine.order.api.dto;

import java.util.List;

public record ValidationResultResponse(
    boolean valid,
    boolean validItems,
    boolean validAddress,
    List<String> errors
){
}
