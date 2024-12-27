package potatoes.server.utils.crypto;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CookieProvider {
	@Value("${cookie.domain}")
	private String domain;

	@Value("${security.jwt.token.expire-length}")
	private String accessTokenExpire;

	public ResponseCookie accessTokenCookie(String accessToken, HttpServletRequest request) {
		String host = request.getServerName();
		boolean isLocalhost = host.contains("localhost") || host.contains("127.0.0.1");

		ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from("accessToken", accessToken)
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(Duration.ofSeconds(Long.parseLong(accessTokenExpire)))
			.sameSite("None");

		if (!isLocalhost) {
			cookieBuilder.domain(domain);
		}

		return cookieBuilder.build();
	}
}
