package potatoes.server.dto;

import potatoes.server.entity.Chat;

public record ChatSummaryResponse(
	String chatName,
	String host,
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
			chat.getName(),
			chat.getHost().getNickname(),
			hasJoined,
			unreadMessageCount,
			lastMessageTime,
			chat.getTravel().getImage(),
			chat.getCurrentMemberCount(),
			chat.getMaxMemberCount()
		);
	}
}
