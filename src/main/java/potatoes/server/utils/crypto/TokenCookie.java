package potatoes.server.utils.crypto;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

public record TokenCookie(
	String accessToken,
	@Value("${cookie.domain}") String domain,
	@Value("${security.jwt.token.expire-length}") String expireLength
) {
	public ResponseCookie generateCookie() {
		return ResponseCookie.from("accessToken", this.accessToken)
			.domain(domain)
			.httpOnly(false) //FIXME true일시 프론트가 토큰을 못읽는다는 문제가 있는데 이야기 해볼것
			.secure(false) // FIXME 백엔드 프론트 모두 https 도입시 none으로 변경
			.path("/")
			.maxAge(Duration.ofSeconds(90))
			.sameSite("Lax")    // FIXME 백엔드 프론트 모두 https 도입시 none으로 변경
			.build();
	}
}
