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
		return ResponseCookie.from("accessToken", accessToken)
			.httpOnly(true)
			.secure(false)
			.path("/")
			.maxAge(Duration.ofSeconds(Long.parseLong(accessTokenExpire)))
			.sameSite("Lax")
			.domain(domain)
			.build();
	}
}
