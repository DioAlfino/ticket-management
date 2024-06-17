package com.tickets.ticketmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.tickets.ticketmanagement.config.RsaConfig;

@SpringBootApplication
@EnableConfigurationProperties(RsaConfig.class)
public class TicketmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketmanagementApplication.class, args);
	}

}
