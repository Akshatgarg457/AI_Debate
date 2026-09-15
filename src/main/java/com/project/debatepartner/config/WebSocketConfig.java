package com.project.debatepartner.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private DebateWebSocketHandler debateWebSocketHandler;

    @Override
    @SuppressWarnings("null")
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(debateWebSocketHandler, "/ws-debate")
                .setAllowedOrigins("*");
        
        registry.addHandler(debateWebSocketHandler, "/ws-debate-sockjs")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
