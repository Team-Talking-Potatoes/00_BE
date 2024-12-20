package potatoes.server.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record TravelPageResponse(
	List<GetMyTravelResponse> content,
	long total,
	int currentPage,
	boolean hasNext
) {
	public static TravelPageResponse from(Page<GetMyTravelResponse> page) {
		return new TravelPageResponse(
			page.getContent(),
			page.getTotalElements(),
			page.getNumber(),
			page.hasNext()
		);
	}
}
