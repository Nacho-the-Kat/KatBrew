package com.katbrew.core.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Konfiguration mit den Resources Handlern.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://bugfixes-and-improvements-v8.katscan.pages.dev", "http://localhost:3000", "https://katscan.xyz")
                .allowedOriginPatterns("*.katscan.pages.dev")
                .allowedMethods("GET", "HEAD", "OPTIONS")
                .allowedHeaders("*");
    }
}
