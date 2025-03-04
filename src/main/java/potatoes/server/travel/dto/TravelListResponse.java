package potatoes.server.travel.dto;

import java.util.List;

public record TravelListResponse(
	List<TravelSummaryResponse> content,
	boolean hasNext,
	int currentPage
) {
}
