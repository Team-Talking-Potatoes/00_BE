package potatoes.server.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record ReviewPageResponse(
	List<GetMyReviewResponse> content,
	int total,
	int currentPage,
	boolean hasNext
) {
	public static ReviewPageResponse from(Page<GetMyReviewResponse> page) {
		return new ReviewPageResponse(
			page.getContent(),
			page.getTotalPages(),
			page.getNumber(),
			page.hasNext()
		);
	}
}
