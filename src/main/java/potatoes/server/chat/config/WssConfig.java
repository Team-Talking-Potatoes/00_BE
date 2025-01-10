package potatoes.server.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.chat.stomp.ChatEventHandler;
import potatoes.server.chat.stomp.CustomHandshakeInterceptor;

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
				try {
					if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
						chatEventHandler.handleConnect(headerAccessor);
					}

					if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
						chatEventHandler.handleSubscribe(headerAccessor);
					}

					return message;
				} catch (Exception e) {
					log.error("WebSocket 오류: {}", e.getMessage(), e);

					// 에러 메시지 생성
					StompHeaderAccessor errorHeaderAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
					errorHeaderAccessor.setSessionId(headerAccessor.getSessionId());
					errorHeaderAccessor.setMessage(e.getMessage());

					// 연결 종료 메시지 전송
					StompHeaderAccessor disconnectHeaderAccessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);
					disconnectHeaderAccessor.setSessionId(headerAccessor.getSessionId());
					MessageHeaders headers = disconnectHeaderAccessor.getMessageHeaders();
					channel.send(MessageBuilder.createMessage(new byte[0], headers));

					throw new MessageDeliveryException(message, e);
				}
			}
		});
	}
}
