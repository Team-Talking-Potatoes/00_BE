package potatoes.server.dto;

import java.util.List;

public record TravelListResponse(
	List<TravelSummaryResponse> content,
	int currentPage
) {
}
