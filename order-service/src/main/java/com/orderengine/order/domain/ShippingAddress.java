package com.orderengine.order.domain;

public record ShippingAddress(
        String line1,
        String line2,
        String city,
        String state,
        String postalCode,
        String country
) {
}
