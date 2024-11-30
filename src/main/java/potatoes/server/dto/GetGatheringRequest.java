package potatoes.server.dto;

import java.util.List;

import lombok.Builder;
import potatoes.server.constant.GatheringType;
import potatoes.server.constant.LocationType;
import potatoes.server.utils.pagination.Paginator;

@Builder
public record GetGatheringRequest(
	List<Long> ids,

	GatheringType type,

	LocationType location,

	String date,

	Long createdBy,

	String sortBy,

	String sortOrder,

	int limit,
	int offset
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
		return sortBy != null ? sortBy : "dateTime";
	}

	@Override
	public String getSortOrder() {
		return sortOrder != null ? sortOrder : "asc";
	}

}
