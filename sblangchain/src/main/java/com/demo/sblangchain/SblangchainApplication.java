package com.demo.sblangchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SblangchainApplication {

	public static void main(String[] args) {
		SpringApplication.run(SblangchainApplication.class, args);
	}

}
