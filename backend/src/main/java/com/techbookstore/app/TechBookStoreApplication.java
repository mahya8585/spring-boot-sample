package com.techbookstore.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TechBookStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechBookStoreApplication.class, args);
    }

}