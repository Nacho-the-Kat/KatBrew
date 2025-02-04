package com.katbrew.helper;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

public class KatBrewWebClient {
    public static WebClient createWebClient() {
        return WebClient
                .builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        //100MB
                        .maxInMemorySize(100 * 1024 * 1024))
                .build();
    }

    public static WebClient createRedirectWebClient() {
        HttpClient httpClient = HttpClient.create().followRedirect(true);
        return WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        //100MB
                        .maxInMemorySize(100 * 1024 * 1024))
                .build();
    }
}
