package com.katbrew.helper;

import org.springframework.web.reactive.function.client.WebClient;

public class KatBrewWebClient {
    public static WebClient createWebClient() {
        return WebClient
                .builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(30 * 1024 * 1024))
                .build();
    }
}
