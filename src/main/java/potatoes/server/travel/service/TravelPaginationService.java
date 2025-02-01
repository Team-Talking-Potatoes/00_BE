package potatoes.server.travel.service;

import static potatoes.server.utils.time.DateTimeUtils.*;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import potatoes.server.travel.bookmark.repository.BookmarkRepository;
import potatoes.server.travel.dto.GetMyTravelResponse;
import potatoes.server.travel.dto.TravelSummaryResponse;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.repository.TravelRepository;
import potatoes.server.travel.repository.TravelUserRepository;
import potatoes.server.utils.constant.TravelSortType;
import potatoes.server.utils.constant.TravelStatus;
import potatoes.server.utils.pagination.dto.PageResponse;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TravelPaginationService {

	private final BookmarkRepository bookmarkRepository;
	private final TravelRepository travelRepository;
	private final TravelUserRepository travelUserRepository;

	public PageResponse<TravelSummaryResponse> getTravelList(
		int page, int size, Boolean isDomestic, String startAt, String endAt,
		TravelSortType sortOrder, String query, Optional<Long> userId
	) {
		Pageable pageable = createPageable(page, size, sortOrder);

		Instant parsedStartAt = startAt != null ? parseYearMonthDay(startAt) : null;
		Instant parsedEndAt = endAt != null ? parseYearMonthDay(endAt) : null;

		Page<Travel> travels = travelRepository.findTravels(
			isDomestic,
			parsedStartAt,
			parsedEndAt,
			query,
			pageable
		);

		Page<TravelSummaryResponse> responsePage = travels.map(travel -> {
			int currentTravelMateCount = (int)travelUserRepository.countByTravel(travel);
			Boolean isBookmark = userId
				.map(uid -> bookmarkRepository.existsByUserIdAndTravelId(uid, travel.getId()))
				.orElse(null);
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
		Page<GetMyTravelResponse> findTravels = travelUserRepository.findMyTravels(request, userId);
		return PageResponse.from(findTravels);
	}

	public PageResponse<GetMyTravelResponse> getTravelsByStatus(
		int page, int size, Long userId, TravelStatus travelStatus
	) {
		PageRequest request = PageRequest.of(page, size);
		Page<GetMyTravelResponse> findTravels = travelUserRepository.findTravelsByStatus(request, userId,
			travelStatus.name());
		return PageResponse.from(findTravels);
	}

	public PageResponse<GetMyTravelResponse> getMyTravelsByBookmark(
		int page, int size, Long userId
	) {
		PageRequest request = PageRequest.of(page, size);
		Page<GetMyTravelResponse> findTravels = bookmarkRepository.findMyTravelsByBookmark(request, userId);
		return PageResponse.from(findTravels);
	}

	public PageResponse<GetMyTravelResponse> getReviewableMyTravels(
		int page, int size, Long userId
	) {
		PageRequest request = PageRequest.of(page, size);
		Page<GetMyTravelResponse> findTravels = travelUserRepository.findReviewableTravels(request, userId);
		return PageResponse.from(findTravels);
	}
}
