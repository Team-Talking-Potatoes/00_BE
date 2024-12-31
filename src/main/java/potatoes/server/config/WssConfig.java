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
					log.info("STOMP CONNECT 시도");

					List<String> cookieList = accessor.getNativeHeader("Cookie");
					log.info("쿠키 목록: {}", cookieList);  // 쿠키 존재 여부 확인

					if (cookieList == null || cookieList.isEmpty()) {
						log.error("쿠키가 없습니다.");
						throw new WeGoException(COOKIE_NOT_FOUND);
					}

					String cookieString = cookieList.getFirst();
					String accessToken = extractAccessToken(cookieString);
					log.info("추출된 토큰: {}", accessToken);

					if (accessToken == null) {
						log.error("액세스 토큰이 없습니다.");
						throw new WeGoException(TOKEN_NOT_FOUND);
					}

					if (!jwtTokenProvider.validateToken(accessToken)) {
						log.error("유효하지 않은 토큰입니다.");
						throw new WeGoException(UNAUTHORIZED);
					}

					Long userId = Long.parseLong(jwtTokenProvider.getPayload(accessToken));
					log.info("인증된 사용자 ID: {}", userId);

					accessor.setUser(new StompUserPrincipal(userId, accessor.getSessionId()));
				}

				return message;
			}

			private String extractAccessToken(String cookieString) {
				log.info("쿠키 문자열: {}", cookieString);
				String[] cookies = cookieString.split(";");
				for (String cookie : cookies) {
					String[] parts = cookie.trim().split("=");
					if (parts.length == 2 && "accessToken".equals(parts[0])) {
						log.info("토큰 추출: {}", parts[1]);
						return parts[1];
					}
				}
				return null;
			}
		});
	}
}