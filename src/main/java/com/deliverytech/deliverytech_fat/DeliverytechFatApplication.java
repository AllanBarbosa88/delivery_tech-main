package com.deliverytech.deliverytech_fat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class DeliverytechFatApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliverytechFatApplication.class, args);
	}

}
