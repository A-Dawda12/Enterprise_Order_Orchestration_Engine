package com.orderengine.fraud.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthProbeController {

    @GetMapping("/v1/health")
    Map<String, String> health() {
        return Map.of("status", "UP", "service", "fraud-service");
    }
}
