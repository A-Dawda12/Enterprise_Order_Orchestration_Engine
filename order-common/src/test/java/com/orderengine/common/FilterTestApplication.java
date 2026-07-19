package com.orderengine.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class FilterTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilterTestApplication.class, args);
    }

    @RestController
    static class TestController {

        @GetMapping("/test")
        public String test() {
            return "ok";
        }

        @GetMapping("/v1/orders/{orderId}")
        public String getOrder(@PathVariable String orderId) {
            return orderId;
        }

    }
}
