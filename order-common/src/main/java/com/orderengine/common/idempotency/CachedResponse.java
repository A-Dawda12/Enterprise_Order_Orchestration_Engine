package com.orderengine.common.idempotency;

public record CachedResponse(int status, String contentType, String body) {
}
