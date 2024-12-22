package potatoes.server.utils.crypto;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class TokenCookie {
	@Value("${cookie.domain}")
	private String domain;

	@Value("${security.jwt.token.expire-length}")
	private String expireLength;

	// factory 메서드로 변경
	public ResponseCookie generateCookie(String accessToken) {
		return ResponseCookie.from("accessToken", accessToken)
			.domain(domain)
			.httpOnly(false)
			.secure(false)
			.path("/")
			.maxAge(Duration.ofSeconds(Long.parseLong(expireLength)))
			.sameSite("Lax")
			.build();
	}
}
