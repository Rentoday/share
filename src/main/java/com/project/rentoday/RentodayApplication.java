package com.project.rentoday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.project.rentoday")
@EnableJpaRepositories(basePackages = "com.project.rentoday.domain")
public class RentodayApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentodayApplication.class, args);
	}

}
