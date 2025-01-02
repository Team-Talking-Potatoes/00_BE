package potatoes.server.config;

import static potatoes.server.error.ErrorCode.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.error.exception.WeGoException;
import potatoes.server.utils.stomp.ChatEventHandler;
import potatoes.server.utils.stomp.CustomHandshakeInterceptor;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WssConfig implements WebSocketMessageBrokerConfigurer {

	private final ChatEventHandler chatEventHandler;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/sub");
		registry.setApplicationDestinationPrefixes("/pub");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
			.addInterceptors(new CustomHandshakeInterceptor())
			.setAllowedOrigins("*");
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(
					message, StompHeaderAccessor.class);

				if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
					log.info("Connect");
					chatEventHandler.handleConnect(headerAccessor);
				}

				if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
					log.info("Subscribe");
					try {
						chatEventHandler.handleSubscribe(headerAccessor);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						throw new WeGoException(STOMP_SUBSCRIBE_FAILED);
					}
				}

				return message;
			}
		});
	}
}