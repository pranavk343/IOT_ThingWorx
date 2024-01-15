package com.whirlpool.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HDOrderApplication {

	public static void main(String[] args) {
		System.out.println("Starting order API Application");
		SpringApplication.run(HDOrderApplication.class, args);
	}

}
