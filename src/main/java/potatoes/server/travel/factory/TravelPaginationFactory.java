package potatoes.server.travel.factory;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import potatoes.server.travel.bookmark.repository.BookmarkRepository;
import potatoes.server.travel.dto.GetMyTravelResponse;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.repository.TravelRepository;
import potatoes.server.travel.repository.TravelUserRepository;
import potatoes.server.utils.constant.TravelStatus;

@Component
@RequiredArgsConstructor
public class TravelPaginationFactory {

	private final TravelRepository travelRepository;
	private final TravelUserRepository travelUserRepository;
	private final BookmarkRepository bookmarkRepository;

	public Page<Travel> findTravels(Pageable pageable, Boolean isDomestic, Instant startAt, Instant endAt,
		String query) {
		return travelRepository.findTravels(isDomestic, startAt, endAt, query, pageable);
	}

	public Page<GetMyTravelResponse> findMyTravels(Pageable pageable, Long userId) {
		return travelUserRepository.findMyTravels(pageable, userId);
	}

	public Page<GetMyTravelResponse> findTravelsByStatus(Pageable pageable, Long userId, TravelStatus travelStatus) {
		return travelUserRepository.findTravelsByStatus(pageable, userId, travelStatus.name());
	}

	public Page<GetMyTravelResponse> findTravelsByBookmark(Pageable pageable, Long userId) {
		return bookmarkRepository.findMyTravelsByBookmark(pageable, userId);
	}

	public Page<GetMyTravelResponse> findReviewableTravels(Pageable pageable, Long userId) {
		return travelUserRepository.findReviewableTravels(pageable, userId);
	}
}
