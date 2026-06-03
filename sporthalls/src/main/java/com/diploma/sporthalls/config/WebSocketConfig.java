package com.diploma.sporthalls.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Ендпоинтът, към който Android ще се свърже първоначално: ws://localhost:8080/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // Позволява връзки от всякакви устройства (вкл. емулатора)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Дефинираме префикс за дестинациите, които брокерът управлява (за абониране)
        registry.enableSimpleBroker("/topic", "/queue");

        // Префикс за съобщенията, които идват ОТ Android към Сървъра
        registry.setApplicationDestinationPrefixes("/app");
    }


}
