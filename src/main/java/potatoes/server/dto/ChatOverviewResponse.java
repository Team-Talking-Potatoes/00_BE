package potatoes.server.dto;

import java.util.List;

public record ChatOverviewResponse(
	List<ParticipantsInfoResponse> participants,
	List<ChatAlbumResponse> album
) {
}
