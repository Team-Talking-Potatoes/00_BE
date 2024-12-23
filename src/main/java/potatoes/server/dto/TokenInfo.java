package potatoes.server.dto;

public record TokenInfo(
	String accessToken,
	String refreshToken
) {
}

