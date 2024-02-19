package com.continous.lock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.continous.lock.*"})
public class LockApplication {
	public static void main(String[] args) {
		SpringApplication.run(LockApplication.class, args);
	}
}
