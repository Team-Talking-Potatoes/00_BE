package potatoes.server.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import potatoes.server.utils.Pagination.Paginator;

public record GetJoinedGatheringRequest(
	Boolean completed,
	Boolean reviewed,

	@Min(value = 1, message = "Limit의 최소값은 1입니다.")
	int limit,

	@Min(value = 0, message = "offset의 최소값은 0입니다.")
	int offset,

	@Pattern(regexp = "^(dateTime)$", message = "정렬 기준은 'dateTime'만 가능합니다")
	String sortBy,

	@Pattern(regexp = "^(asc|desc)$", message = "정렬 순서는 'asc' 또는 'desc'여야 합니다")
	String sortOrder
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
		return sortBy != null ? sortBy : "dateTime";
	}

	@Override
	public String getSortOrder() {
		return sortOrder != null ? sortOrder : "asc";
	}
}
