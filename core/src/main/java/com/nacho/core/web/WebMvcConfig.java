//package com.nacho.core.web;
//
//import lombok.SneakyThrows;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.util.Arrays;
//
///**
// * Konfiguration mit den Resources Handlern.
// */
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//
//    @Value("${filesystem.static-path}")
//    private String staticPath;
//
//    @SneakyThrows
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // registry für statische resourcen.
//        registry
//                .addResourceHandler("/static/**")
//                .addResourceLocations("file:" + staticPath);
//    }
//
////    @Override
////    public void addCorsMappings(CorsRegistry registry) {
////        registry.addMapping("/**")
////                .allowedOrigins("https://bugfixes-and-improvements-v8.katscan.pages.dev", "http://localhost:3000", "https://katscan.xyz")
////                .allowedOriginPatterns("*.katscan.pages.dev")
////                .allowedMethods("GET", "HEAD", "OPTIONS")
////                .allowedHeaders("*");
////    }
//}
