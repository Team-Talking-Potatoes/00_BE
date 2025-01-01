package potatoes.server.dto;

import potatoes.server.entity.ChatMessage;
import potatoes.server.utils.time.DateTimeUtils;

public record ChatMessageResponse(
	Long chatMessageId,
	String Sender,
	boolean isImage,
	String content,
	String createdAt,
	long unreadCount
) {
	public static ChatMessageResponse of(ChatMessage message, long unreadCount) {
		return new ChatMessageResponse(
			message.getId(),
			message.getSender().getNickname(),
			message.isImage(),
			message.getMessage(),
			DateTimeUtils.getYearMonthDayTime(message.getCreatedAt()),
			unreadCount
		);
	}
}
