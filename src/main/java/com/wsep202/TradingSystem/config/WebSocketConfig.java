package com.wsep202.TradingSystem.config;

import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import com.wsep202.TradingSystem.web.controllers.shakeHandler.CustomHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	/**
	 * define whe app prefix and destination prefix
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED);
		config.setApplicationDestinationPrefixes(PublicApiPaths.APP_DESTINATION_PREFIXED);
	}

	/**
	 *SockJS fallback options so that alternate transports can be used if WebSocket is not available.
	 * The SockJS client will attempt to connect to stompEndpoint and use the best available transport
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint(PublicApiPaths.STOMP_ENDPOINT)
				.setAllowedOrigins("http://localhost:4200")
				.setHandshakeHandler(new CustomHandshakeHandler());

		registry.addEndpoint(PublicApiPaths.STOMP_ENDPOINT)
				.setAllowedOrigins("http://localhost:4200")
				.setHandshakeHandler(new CustomHandshakeHandler())
				.withSockJS();
	}
}
