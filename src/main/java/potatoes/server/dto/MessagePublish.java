package potatoes.server.dto;

public record MessagePublish(
	String[] images,
	String message
) {
}
