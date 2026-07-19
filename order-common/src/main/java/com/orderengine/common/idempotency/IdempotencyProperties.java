package com.orderengine.common.idempotency;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "orderengine.idempotency")
public class IdempotencyProperties {

    private boolean enabled = true;
    private Duration ttl = Duration.ofHours(24);

    public boolean isEnable() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Duration getTtl() {
        return ttl;
    }

    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }
}
