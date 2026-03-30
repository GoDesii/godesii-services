package com.godesii.godesii_services.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for bidirectional STOMP messaging.
 *
 * <p>Endpoints:
 * <ul>
 *   <li><b>/ws</b> — STOMP WebSocket handshake endpoint (with SockJS fallback)</li>
 * </ul>
 *
 * <p>Broker destinations:
 * <ul>
 *   <li><b>/topic/...</b>  — broadcast to all subscribers (e.g. order status updates)</li>
 *   <li><b>/queue/...</b>  — point-to-point messages (e.g. user-specific notifications)</li>
 * </ul>
 *
 * <p>Application destinations:
 * <ul>
 *   <li><b>/app/...</b> — prefix for messages sent from client to server-side @MessageMapping handlers</li>
 * </ul>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable simple in-memory broker for /topic (broadcast) and /queue (point-to-point)
        registry.enableSimpleBroker("/topic", "/queue");

        // Prefix for @MessageMapping methods in controllers
        registry.setApplicationDestinationPrefixes("/app");

        // Prefix for user-specific destinations (e.g. /user/{userId}/queue/notifications)
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // STOMP endpoint with SockJS fallback for browser compatibility
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // Raw WebSocket endpoint (no SockJS) for native clients (mobile apps, etc.)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }
}
