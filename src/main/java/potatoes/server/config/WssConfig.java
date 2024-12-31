package potatoes.server.config;

import static potatoes.server.error.ErrorCode.*;

import java.util.List;

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
import potatoes.server.utils.stomp.StompUserPrincipal;
import potatoes.server.utils.jwt.JwtTokenUtil;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WssConfig implements WebSocketMessageBrokerConfigurer {

	private final JwtTokenUtil jwtTokenProvider;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/sub");
		registry.setApplicationDestinationPrefixes("/pub");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
			.setAllowedOrigins("*");
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
					message, StompHeaderAccessor.class);

				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					List<String> cookieList = accessor.getNativeHeader("Cookie");

					if (cookieList == null || cookieList.isEmpty()) {
						throw new WeGoException(COOKIE_NOT_FOUND);
					}
					String cookieString = cookieList.getFirst();
					String accessToken = extractAccessToken(cookieString);
					if (accessToken == null) {
						throw new WeGoException(TOKEN_NOT_FOUND);
					}

					if (!jwtTokenProvider.validateToken(accessToken)) {
						throw new WeGoException(UNAUTHORIZED);
					}

					Long userId = Long.parseLong(jwtTokenProvider.getPayload(accessToken));
					accessor.setUser(new StompUserPrincipal(userId, accessor.getSessionId()));
				}

				return message;
			}

			private String extractAccessToken(String cookieString) {
				String[] cookies = cookieString.split(";");
				for (String cookie : cookies) {
					String[] parts = cookie.trim().split("=");
					if (parts.length == 2 && "accessToken".equals(parts[0])) {
						return parts[1];
					}
				}
				return null;
			}
		});
	}
}