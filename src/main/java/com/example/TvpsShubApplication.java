package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.example", "bdUtil" })
public class TvpsShubApplication {

    public static void main(String[] args) {
        SpringApplication.run(TvpsShubApplication.class, args);
    }
}
