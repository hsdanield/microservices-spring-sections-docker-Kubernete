package com.microservice.configserverlocalfile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigserverlocalfileApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigserverlocalfileApplication.class, args);
	}

}
