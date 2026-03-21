package com.akine_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AkineApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AkineApiApplication.class, args);
	}

}
