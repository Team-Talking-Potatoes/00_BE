package potatoes.server.travel.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import potatoes.server.travel.bookmark.factory.BookmarkFactory;
import potatoes.server.travel.domain.query.BookmarkQuery;
import potatoes.server.travel.domain.query.TravelQuery;
import potatoes.server.travel.domain.query.TravelUserQuery;
import potatoes.server.travel.dto.GetMyTravelResponse;
import potatoes.server.travel.dto.TravelSummaryResponse;
import potatoes.server.travel.entity.Travel;
import potatoes.server.utils.constant.TravelSortType;
import potatoes.server.utils.constant.TravelStatus;
import potatoes.server.utils.pagination.dto.PageResponse;
import potatoes.server.utils.time.DateTimeUtils;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TravelPaginationService {

	private final BookmarkFactory bookmarkFactory;
	private final BookmarkQuery bookmarkQuery;
	private final TravelUserQuery travelUserQuery;
	private final TravelQuery travelQuery;

	public PageResponse<TravelSummaryResponse> getTravelList(
		int page, int size, Boolean isDomestic, String startAt, String endAt,
		TravelSortType sortOrder, String query, Optional<Long> userId
	) {
		Pageable pageable = createPageable(page, size, sortOrder);

		Instant parsedStartAt = DateTimeUtils.parseYearMonthDayOrNull(startAt);
		Instant parsedEndAt = DateTimeUtils.parseYearMonthDayOrNull(endAt);

		Page<Travel> travels = travelQuery.findTravels(pageable, isDomestic, parsedStartAt, parsedEndAt,
			query);

		Page<TravelSummaryResponse> responsePage = travels.map(travel -> {
			int currentTravelMateCount = (int)travelUserQuery.countParticipants(travel);

			Boolean isBookmark = bookmarkFactory.isUserParticipating(userId, travel.getId());
			return TravelSummaryResponse.from(travel, currentTravelMateCount, isBookmark);
		});

		return PageResponse.from(responsePage);
	}

	private Pageable createPageable(int page, int size, TravelSortType sortOrder) {
		Sort sort = Sort.by(
			sortOrder == TravelSortType.registrationEnd
				? Sort.Direction.ASC
				: Sort.Direction.DESC,
			getSortField(sortOrder)
		);
		return PageRequest.of(page, size, sort);
	}

	private String getSortField(TravelSortType sortOrder) {
		return switch (sortOrder) {
			// TODO - 조회수를 저장하는 로직이 없어 popular조건을 추가해야됨
			case recent, popular -> "createdAt";
			case registrationEnd -> "registrationEnd";
		};
	}

	public PageResponse<GetMyTravelResponse> getMyTravels(int page, int size, Long userId) {
		PageRequest request = PageRequest.of(page, size);
		Page<GetMyTravelResponse> findTravels = travelUserQuery.findMyTravels(request, userId);
		return PageResponse.from(findTravels);
	}

	public PageResponse<GetMyTravelResponse> getTravelsByStatus(
		int page, int size, Long userId, TravelStatus travelStatus
	) {
		PageRequest request = PageRequest.of(page, size);
		Page<GetMyTravelResponse> findTravels = travelUserQuery.findTravelsByStatus(request, userId,
			travelStatus);
		return PageResponse.from(findTravels);
	}

	public PageResponse<GetMyTravelResponse> getMyTravelsByBookmark(
		int page, int size, Long userId
	) {
		PageRequest request = PageRequest.of(page, size);
		Page<GetMyTravelResponse> findTravels = bookmarkQuery.findTravelsByBookmark(request, userId);
		return PageResponse.from(findTravels);
	}

	public PageResponse<GetMyTravelResponse> getReviewableMyTravels(
		int page, int size, Long userId
	) {
		PageRequest request = PageRequest.of(page, size);
		Page<GetMyTravelResponse> findTravels = travelUserQuery.findReviewableTravels(request, userId);
		return PageResponse.from(findTravels);
	}
}
