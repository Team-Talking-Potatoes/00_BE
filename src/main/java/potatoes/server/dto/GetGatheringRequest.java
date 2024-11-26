package potatoes.server.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

import javax.swing.*;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import potatoes.server.constant.GatheringType;
import potatoes.server.constant.SortByType;
import potatoes.server.utils.Pagination.Paginator;

public record GetGatheringRequest(
	String id,

	GatheringType type,

	String location,

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate date,

	Long createdBy,

	SortByType sortBy,

	SortOrder sortOrder,

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

	public Instant getStartOfDay() {
		return date != null
			? date.atStartOfDay(ZoneOffset.UTC).toInstant()
			: null;
	}

	// 해당 날짜의 끝 시간 (2024-07-06 23:59:59.999999999 UTC)
	public Instant getEndOfDay() {
		return date != null
			? date.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC)
			: null;
	}
}
