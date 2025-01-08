package potatoes.server.dto;

import java.util.List;

import potatoes.server.chat.entity.ChatMessage;
import potatoes.server.entity.User;
import potatoes.server.utils.time.DateTimeUtils;

public record MessageSubscribe(
	Long chatMessageId,
	List<String> images,
	String content,
	String sender,
	String senderProfileImage,
	String createdAt,
	int unreadCount
) {
	public static MessageSubscribe of(ChatMessage chatMessage, List<String> images, User user, int unreadCount) {
		return new MessageSubscribe(
			chatMessage.getId(),
			images,
			chatMessage.getMessage(),
			user.getNickname(),
			user.getProfileImage(),
			DateTimeUtils.getYearMonthDayTime(chatMessage.getCreatedAt()),
			unreadCount
		);
	}
}
