package com.nacho.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.nacho")
@ConfigurationProperties("com.nacho.core.properties")
public class KRC20BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.nacho.demo.KRC20BackendApplication.class, args);
    }

}
