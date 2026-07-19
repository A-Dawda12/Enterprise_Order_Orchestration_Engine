package com.orderengine.common.idempotency;

import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.OrderEngineException;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;


import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class IdempotencyResponseStore {

    static final String PROCESSING = "__PROCESSING__";

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    private final String keyPrefix;

    private final Duration ttl;

    public IdempotencyResponseStore(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            String serviceName,
            Duration ttl
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.keyPrefix = serviceName + ":idempotency";
        this.ttl = ttl;
    }

    public Optional<CachedResponse> find(String idempotency) {
        try {
            String value = redisTemplate.opsForValue().get(redisKey(idempotency));
            if(value == null || PROCESSING.equals(value)) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(value, CachedResponse.class));
        } catch (Exception ex) {
            throw redisUnavailable(ex);
        }
    }

    public boolean tryAcquire(String idempotency) {
        try {
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(redisKey(idempotency), PROCESSING, ttl);
            return Boolean.TRUE.equals(acquired);
        } catch (Exception ex) {
            throw redisUnavailable(ex);
        }
    }

    public boolean isProcessing(String idempotencyKey) {
        try{
            return PROCESSING.equals(redisTemplate.opsForValue().get(redisKey(idempotencyKey)));
        } catch (Exception ex) {
            throw redisUnavailable(ex);
        }
    }

    public void save(String idempotencyKey, CachedResponse response) {
        try{
            redisTemplate.opsForValue().set(redisKey(idempotencyKey), objectMapper.writeValueAsString(response), ttl);
        }
        catch (JsonProcessingException ex) {
            throw new OrderEngineException(ErrorCode.INTERNAL_ERROR, "Failed to cache idempoten rtesponse");
        } catch (Exception ex) {
            throw redisUnavailable(ex);
        }
    }

    public void release(String idempotencyKey) {
        try {
            redisTemplate.delete(redisKey(idempotencyKey));
        } catch (Exception ex) {
            throw redisUnavailable(ex);
        }
    }

    private String redisKey(String idempotencyKey) {
        return keyPrefix + ":" + idempotencyKey;
    }

    private OrderEngineException redisUnavailable(Exception ex) {
        return new OrderEngineException(
                ErrorCode.SERVICE_UNAVAILABLE,
                "Idempotency store unavailable",
                List.of());
    }
}
