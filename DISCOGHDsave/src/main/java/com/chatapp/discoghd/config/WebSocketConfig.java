package com.chatapp.discoghd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Activer un broker simple sur /topic
        config.enableSimpleBroker("/topic", "/queue");
        // Préfixe pour envoyer des messages (ex: /app/sendMessage)
        config.setApplicationDestinationPrefixes("/app");
        // Pour les messages privés
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Point d'entrée du WebSocket (SockJS)
        registry.addEndpoint("/chat-websocket").withSockJS();
    }
}
