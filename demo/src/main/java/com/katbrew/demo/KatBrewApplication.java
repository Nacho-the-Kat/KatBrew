package com.katbrew.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.katbrew")
@ConfigurationProperties("com.katbrew.core.properties")
public class KatBrewApplication {

    public static void main(String[] args) {
        SpringApplication.run(KatBrewApplication.class, args);
    }

}
