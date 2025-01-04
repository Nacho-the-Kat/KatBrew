package com.katbrew.core.web;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Konfiguration mit den Resources Handlern.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${filesystem.static-path}")
    private String staticPath;

    @SneakyThrows
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // registry für statische resourcen.
        registry
                .addResourceHandler("/static/**")
                .addResourceLocations("file:" + staticPath);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://*.katscan.pages.dev","https://katscan.xyz", "http://localhost:3000")
                .allowedOriginPatterns("*.katscan.pages.dev")
                .allowedMethods("GET","POST", "HEAD", "OPTIONS")
                .allowedHeaders("*");
    }
}
