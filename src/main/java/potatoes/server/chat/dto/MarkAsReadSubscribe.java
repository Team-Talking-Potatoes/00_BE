package potatoes.server.chat.dto;

public record MarkAsReadSubscribe(
	Long chatMessageId,
	Long unreadCount
) {
}
