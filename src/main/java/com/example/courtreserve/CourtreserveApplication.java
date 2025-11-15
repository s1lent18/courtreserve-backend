package com.example.courtreserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class CourtreserveApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourtreserveApplication.class, args);
	}
}