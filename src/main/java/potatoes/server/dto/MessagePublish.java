package potatoes.server.dto;

public record MessagePublish(
	boolean isImage,
	String message
) {
}
