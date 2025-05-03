package com.example.demo;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
      })
@Slf4j

public class Auction12Application {
	public static void printCurrentTimeInIST() {
        ZonedDateTime nowInIST = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
      
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        log.info("Current time in IST: " + nowInIST.format(formatter));
        System.out.println("Current time in IST: " + nowInIST.format(formatter));
    }
	
	public static void main(String[] args) {
		   
		   
		SpringApplication.run(Auction12Application.class, args);
	}
	
	
	
}
