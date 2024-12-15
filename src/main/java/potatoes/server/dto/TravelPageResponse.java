package potatoes.server.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record TravelPageResponse(
	List<GetMyTravelResponse> content,
	int total,
	int currentPage,
	boolean hasNext
) {
	public static TravelPageResponse from(Page<GetMyTravelResponse> page) {
		return new TravelPageResponse(
			page.getContent(),
			page.getTotalPages(),
			page.getNumber(),
			page.hasNext()
		);
	}
}
