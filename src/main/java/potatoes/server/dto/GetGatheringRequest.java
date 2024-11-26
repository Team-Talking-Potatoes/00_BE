package potatoes.server.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import potatoes.server.constant.GatheringType;
import potatoes.server.constant.SortByType;
import potatoes.server.utils.Pagination.Paginator;

@Builder
public record GetGatheringRequest(
	List<Long> ids,

	GatheringType type,

	String location,

	String date,

	Long createdBy,

	SortByType sortBy,

	String sortOrder,

	@Min(value = 1, message = "Limit의 최소값은 1입니다.") int limit,

	@Min(value = 0, message = "offset의 최소값은 0입니다.") int offset
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
		return sortBy != null ? sortBy.name() : "dateTime";
	}

	@Override
	public String getSortOrder() {
		return sortOrder != null ? sortOrder.name() : "desc";
	}

}
