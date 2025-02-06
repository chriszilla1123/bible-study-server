package com.chillteq.bible_study_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.chillteq.bible_study_server")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
