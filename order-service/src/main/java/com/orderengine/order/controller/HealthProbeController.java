package com.orderengine.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "health", description = "Health probe for the order service")
public class HealthProbeController {

    @GetMapping("/v1/health")
    @Operation(summary = "Health probe", description = "Returns the health status of the order service")
    Map<String, String> health() {
        return Map.of("status", "UP", "service", "order-service");
    }
}
