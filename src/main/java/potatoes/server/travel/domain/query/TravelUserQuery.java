package potatoes.server.travel.domain.query;

import static potatoes.server.utils.error.ErrorCode.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import potatoes.server.travel.dto.GetMyTravelResponse;
import potatoes.server.travel.dto.ParticipantResponse;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.entity.TravelUser;
import potatoes.server.travel.repository.TravelUserRepository;
import potatoes.server.utils.constant.TravelStatus;
import potatoes.server.utils.error.exception.WeGoException;

@RequiredArgsConstructor
@Component
public class TravelUserQuery {

	private final TravelUserRepository travelUserRepository;

	public TravelUser findTravelUser(Long travelId, Long userId) {
		return travelUserRepository.findByTravelIdAndUserId(travelId, userId)
			.orElseThrow(() -> new WeGoException(TRAVEL_NOT_FOUND));
	}

	public long countParticipants(Travel travel) {
		return travelUserRepository.countByTravel(travel);
	}

	public Boolean isUserParticipating(Long travelId, Optional<Long> userId) {
		return userId
			.map(uid -> travelUserRepository.existsByTravelIdAndUserId(travelId, uid))
			.orElse(null);
	}

	public Boolean isUserParticipating(Long travelId, Long userId) {
		return isUserParticipating(travelId, Optional.of(userId));
	}

	public List<ParticipantResponse> findAllParticipants(Travel travel) {
		return travelUserRepository.findAllByTravel(travel).stream()
			.map(ParticipantResponse::from)
			.toList();
	}

	public Page<GetMyTravelResponse> findMyTravels(Pageable pageable, Long userId) {
		return travelUserRepository.findMyTravels(pageable, userId);
	}

	public Page<GetMyTravelResponse> findTravelsByStatus(Pageable pageable, Long userId, TravelStatus travelStatus) {
		return travelUserRepository.findTravelsByStatus(pageable, userId, travelStatus.name());
	}

	public Page<GetMyTravelResponse> findReviewableTravels(Pageable pageable, Long userId) {
		return travelUserRepository.findReviewableTravels(pageable, userId);
	}
}
