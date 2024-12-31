package potatoes.server.dto;

public record MarkAsReadSubscribe(
	Long chatMessageId,
	Long unreadCount
) {
}
