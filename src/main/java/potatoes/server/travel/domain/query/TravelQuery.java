package potatoes.server.travel.domain.query;

import static potatoes.server.utils.error.ErrorCode.*;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import potatoes.server.travel.dto.TravelPlanResponse;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.repository.TravelPlanRepository;
import potatoes.server.travel.repository.TravelRepository;
import potatoes.server.utils.error.exception.WeGoException;

@RequiredArgsConstructor
@Component
public class TravelQuery {

	private final TravelRepository travelRepository;
	private final TravelPlanRepository travelPlanRepository;

	public Travel findTravel(Long travelId) {
		return travelRepository.findById(travelId)
			.orElseThrow(() -> new WeGoException(TRAVEL_NOT_FOUND));
	}

	public List<Travel> findTop8ByOrderByIdDesc() {
		return travelRepository.findTop8ByOrderByIdDesc();
	}

	public List<TravelPlanResponse> findAllTravelPlans(Travel travel) {
		return travelPlanRepository.findAllByTravel(travel).stream()
			.map(TravelPlanResponse::from)
			.toList();
	}

	public Page<Travel> findTravels(Pageable pageable, Boolean isDomestic, Instant startAt, Instant endAt,
		String query) {
		return travelRepository.findTravels(isDomestic, startAt, endAt, query, pageable);
	}
}
