package potatoes.server.chat.dto;

import java.util.List;

public record RecentChatResponse(
	String chatTitle,
	List<MessageSubscribe> chatMessages
) {
}
