package potatoes.server.utils.stomp;

import static potatoes.server.utils.error.ErrorCode.*;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.utils.error.exception.WeGoException;

@Slf4j
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Map<String, Object> attributes) {

		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest)request;
			HttpServletRequest httpServletRequest = servletRequest.getServletRequest();

			Cookie[] cookies = httpServletRequest.getCookies();
			if (cookies == null) {
				throw new WeGoException(COOKIE_NOT_FOUND);
			}

			String accessToken = null;
			for (Cookie cookie : cookies) {
				if ("accessToken".equals(cookie.getName())) {
					accessToken = cookie.getValue();
					break;
				}
			}
			if (accessToken != null) {
				attributes.put("accessToken", accessToken);
			} else {
				log.info("no cookie");
			}
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Exception exception) {

	}

}
