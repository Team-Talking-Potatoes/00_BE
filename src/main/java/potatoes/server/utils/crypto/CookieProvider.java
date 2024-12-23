package potatoes.server.utils.crypto;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import potatoes.server.dto.TokenInfo;
import potatoes.server.dto.TokenResponse;

@Component
public class CookieProvider {
	@Value("${cookie.domain}")
	private String domain;

	@Value("${security.jwt.token.access-token-expire}")
	private String accessTokenExpire;

	@Value("${security.jwt.token.refresh-token-expire}")
	private String refreshTokenExpire;

	public TokenResponse generateTokenCookies(TokenInfo tokenInfo) {
		ResponseCookie accessTokenCookie = accessTokenCookie(tokenInfo.accessToken());
		ResponseCookie refreshTokenCookie = refreshTokenCookie(tokenInfo.refreshToken());

		return new TokenResponse(accessTokenCookie, refreshTokenCookie);
	}

	public ResponseCookie accessTokenCookie(String accessToken) {
		return ResponseCookie.from("accessToken", accessToken)
			// .domain(domain)
			.httpOnly(false)
			.secure(false)
			.path("/")
			.maxAge(Duration.ofSeconds(Long.parseLong(accessTokenExpire)))
			.sameSite("Lax")
			.build();
	}

	public ResponseCookie refreshTokenCookie(String refreshToken) {
		return ResponseCookie.from("refreshToken", refreshToken)
			// .domain(domain) //FIXME domain입력시 local에서 테스트못함
			.httpOnly(true)
			.secure(false)
			.path("/")
			.maxAge(Duration.ofSeconds(Long.parseLong(refreshTokenExpire)))
			.sameSite("Lax")
			.build();
	}
}
