package com.project.rentoday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "com.project.rentoday")
public class RentodayApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentodayApplication.class, args);
	}


}

