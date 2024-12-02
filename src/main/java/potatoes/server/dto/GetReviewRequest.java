package potatoes.server.dto;

import lombok.Builder;
import potatoes.server.constant.GatheringType;
import potatoes.server.constant.LocationType;
import potatoes.server.utils.pagination.Paginator;

@Builder
public record GetReviewRequest(
	Long gatheringId,
	Long userId,
	GatheringType type,
	LocationType location,
	String date,
	String dueDate,
	int limit,
	int offset,
	String sortBy,
	String sortOrder
) implements Paginator {
	@Override
	public int getOffset() {
		return this.offset;
	}

	@Override
	public int getLimit() {
		return this.limit;
	}

	@Override
	public String getSortBy() {
		return this.sortBy();
	}

	@Override
	public String getSortOrder() {
		return sortOrder != null ? sortOrder() : "asc";
	}

}
