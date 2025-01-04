package potatoes.server.dto;

public record AlarmSubscribe(
	boolean isNewMessage,
	Long chatId,
	int currentMemberCount,
	String sendAt
) {
}
