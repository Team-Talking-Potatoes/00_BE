package potatoes.server.mail.dto;

public record VerifyResponse(
	String emailVerifyToken
) {
}
