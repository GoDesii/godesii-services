package com.godesii.godesii_services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.godesii.godesii_services"})
@EnableJpaRepositories(basePackages = {
		"com.godesii.godesii_services.repository"
})
public class GodesiiServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(GodesiiServicesApplication.class, args);
	}

}
