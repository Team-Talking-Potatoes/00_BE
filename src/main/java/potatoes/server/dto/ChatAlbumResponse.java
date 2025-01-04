package potatoes.server.dto;

import static potatoes.server.utils.time.DateTimeUtils.*;

import java.util.List;

import potatoes.server.entity.ChatImage;
import potatoes.server.entity.ChatMessage;

public record ChatAlbumResponse(
	List<String> images,
	String uploadDate,
	String uploader
) {
	public static ChatAlbumResponse from(ChatMessage chatMessage) {
		return new ChatAlbumResponse(
			chatMessage.getChatImages().stream()
				.map(ChatImage::getImageUrl)
				.toList(),
			getYearMonthDayTime(chatMessage.getCreatedAt()),
			chatMessage.getSender().getNickname()
		);
	}
}
