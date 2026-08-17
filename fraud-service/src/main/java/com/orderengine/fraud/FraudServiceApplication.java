package com.orderengine.fraud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.orderengine.fraud",
		"com.orderengine.common.web"
})
public class FraudServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(FraudServiceApplication.class, args);
	}

}
