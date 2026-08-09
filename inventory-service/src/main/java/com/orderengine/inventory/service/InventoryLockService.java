package com.orderengine.inventory.service;

import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.OrderEngineException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class InventoryLockService {

    private static final Duration LOCK_TTL = Duration.ofSeconds(300);
    private static final String KEY_PREFIX = "inventory:lock";

    private final StringRedisTemplate redisTemplate;

    public InventoryLockService(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public List<String> acquireAll(Set<String> skus, String orderId) {
        List<String> acquired = new ArrayList<>();
        try{
            for(String sku : new LinkedHashSet<>(skus)) {
                Boolean ok = redisTemplate.opsForValue()
                        .setIfAbsent(KEY_PREFIX + sku, orderId, LOCK_TTL);
                if(!Boolean.TRUE.equals(ok)) {
                    throw new OrderEngineException(
                            ErrorCode.CONFLICT,
                            "Could not acquire inventory lock for sku = " + sku
                    );
                }
                acquired.add(sku);
            }
            return acquired;
        }catch(OrderEngineException ex) {
            releaseAll(acquired);
            throw ex;
        }
    }

    public void releaseAll(List<String> skus) {
        for(String sku : skus) {
            redisTemplate.delete(KEY_PREFIX + sku);
        }
    }

}
