package potatoes.server.dto;

public record AlarmSubscribe(
	Long chatId,
	int currentMemberCount,
	String sendAt
) {
}
