package potatoes.server.dto;

import lombok.Builder;
import potatoes.server.utils.Pagination.Paginator;

@Builder
public record GetGatheringParticipantRequest(
	Long userId,
	Long gatheringId,
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
