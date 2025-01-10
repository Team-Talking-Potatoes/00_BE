package potatoes.server.chat.dto;

import potatoes.server.utils.constant.AlarmStatus;

public record AlarmSubscribe(
	Long chatId,
	int currentMemberCount,
	String sendAt,
	AlarmStatus status,
	ParticipantsInfoResponse participant
) {
}
