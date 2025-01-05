package potatoes.server.dto;

import potatoes.server.constant.AlarmStatus;

public record AlarmSubscribe(
	Long chatId,
	int currentMemberCount,
	String sendAt,
	AlarmStatus status,
	ParticipantsInfoResponse participant
) {
}
