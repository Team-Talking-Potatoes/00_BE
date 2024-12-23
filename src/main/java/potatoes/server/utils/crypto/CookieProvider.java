package potatoes.server.utils.crypto;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieProvider {
	@Value("${cookie.domain}")
	private String domain;

	@Value("${security.jwt.token.expire-length}")
	private String accessTokenExpire;

	public ResponseCookie accessTokenCookie(String accessToken) {
		return ResponseCookie.from("accessToken", accessToken)
			.domain(domain)
			.httpOnly(true)
			.secure(false)
			.path("/")
			.maxAge(Duration.ofSeconds(Long.parseLong(accessTokenExpire)))
			.sameSite("Lax")
			.build();
	}
}
