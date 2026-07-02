package com.deliverytech.deliverytech_fat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class DeliverytechApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliverytechApplication.class, args);
	}

}
