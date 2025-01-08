package potatoes.server.dto;

import potatoes.server.chat.entity.Chat;

public record ChatSummaryResponse(
	Long chatId,
	String chattingName,
	String description,
	String host,
	String hostProfileImage,
	boolean hasJoined,
	long unreadMessageCount,
	String lastMessageTime,
	String image,
	int membersCount,
	int totalMembersCount
) {
	public static ChatSummaryResponse of(Chat chat, boolean hasJoined, long unreadMessageCount,
		String lastMessageTime) {
		return new ChatSummaryResponse(
			chat.getId(),
			chat.getName(),
			chat.getTravel().getDescription(),
			chat.getHost().getNickname(),
			chat.getHost().getProfileImage(),
			hasJoined,
			unreadMessageCount,
			lastMessageTime,
			chat.getTravel().getImage(),
			chat.getCurrentMemberCount(),
			chat.getMaxMemberCount()
		);
	}
}
