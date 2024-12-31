package potatoes.server.dto;

import potatoes.server.entity.ChatImage;
import potatoes.server.utils.time.DateTimeUtils;

public record ChatAlbumResponse(
	String image,
	String uploadDate,
	String uploader
) {
	public static ChatAlbumResponse from(ChatImage chatImage) {
		return new ChatAlbumResponse(
			chatImage.getImageUrl(),
			DateTimeUtils.getYearMonthDayTime(chatImage.getChatMessage().getCreatedAt()),
			chatImage.getChatMessage().getSender().getNickname()
		);
	}
}
