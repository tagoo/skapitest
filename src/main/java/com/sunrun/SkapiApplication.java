package com.sunrun;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SkapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkapiApplication.class, args);
	}
}
