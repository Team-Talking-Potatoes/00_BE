package potatoes.server.dto;

import org.springframework.http.ResponseCookie;

public record TokenResponse(
	ResponseCookie accessTokenCookie,
	ResponseCookie refreshTokenCookie
) {
}
