package com.name.evidenceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class EvidenceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvidenceServiceApplication.class, args);
	}

}
