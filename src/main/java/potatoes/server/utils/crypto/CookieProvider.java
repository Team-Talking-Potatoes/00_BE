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
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(Duration.ofSeconds(Long.parseLong(accessTokenExpire)))
			.sameSite("None")
			.domain(domain)
			.build();
	}

	public ResponseCookie expireAccessTokenCookie() {
		return ResponseCookie.from("accessToken", "")
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(0)
			.sameSite("None")
			.domain(domain)
			.build();
	}
}
