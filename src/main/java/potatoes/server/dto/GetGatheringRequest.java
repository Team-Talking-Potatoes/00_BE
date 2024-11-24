package potatoes.server.dto;

import java.time.Instant;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import potatoes.server.constant.GatheringType;

public record GetGatheringRequest(
	List<Long> ids,

	GatheringType type,

	String location,

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	Instant date,

	Long createdBy,

	String sortBy,

	String sortOrder,

	@Min(value = 1, message = "Limit의 최소값은 1입니다.") int limit,

	@Min(value = 0, message = "offset의 최소값은 0입니다.") int offset) {

	public GetGatheringRequest {
		sortBy = sortBy != null ? sortBy : "dateTime";
		sortOrder = sortOrder != null ? sortOrder : "desc";
		limit = limit == 0 ? 20 : limit;
	}
}
