package potatoes.server.dto;

import java.util.List;

import lombok.Builder;
import potatoes.server.constant.GatheringType;
import potatoes.server.utils.Pagination.Paginator;

@Builder
public record GetGatheringRequest(
	List<Long> ids,

	GatheringType type,

	String location,

	String date,

	Long createdBy,

	String sortBy,

	String sortOrder,

	int limit,

	int offset
) implements Paginator {

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public int getLimit() {
		return limit;
	}

	@Override
	public String getSortBy() {
		return sortBy;
	}

	@Override
	public String getSortOrder() {
		return sortOrder != null ? sortOrder() : "asc";
	}

}
