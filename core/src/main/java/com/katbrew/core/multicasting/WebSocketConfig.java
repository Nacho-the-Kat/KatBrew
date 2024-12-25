package com.katbrew.core.multicasting;

import com.katbrew.services.base.JooqService;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Konfiguratoin für den Multicast Websocket.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Registriert den Stompentpunkt, für den initalen Connect zum Multicast Server.
     *
     * @param registry Die Registry des Stomp Endpunktes.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // Alle Multicast Listener müssen sich über einen SockJS Client am Server anmelden.
        // Die Url ist hierbei "/socketConnect".
        registry.addEndpoint("/socketConnect").withSockJS();
        registry.addEndpoint("/socket");
    }

    /**
     * Konfiguriert den Message Broker, für das Empfangen und Senden von Nachrichten.
     *
     * @param registry Die Registry, des Message Brokers.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(JooqService.MULTICAST_PREFIX);
        registry.setApplicationDestinationPrefixes("/");
    }
}
