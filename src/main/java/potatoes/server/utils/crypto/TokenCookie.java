package potatoes.server.utils.crypto;

import java.time.Duration;

import org.springframework.http.ResponseCookie;

public record TokenCookie(
	String accessToken
) {
	public ResponseCookie generateCookie() {
		return ResponseCookie.from("accessToken", this.accessToken)
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(Duration.ofSeconds(90))
			.sameSite("None")
			.build();
	}
}
