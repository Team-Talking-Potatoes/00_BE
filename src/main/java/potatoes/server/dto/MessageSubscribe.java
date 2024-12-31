package potatoes.server.dto;

public record MessageSubscribe(
	Long chatId,
	boolean isImage,
	String content,
	String createdAt
) {
}
