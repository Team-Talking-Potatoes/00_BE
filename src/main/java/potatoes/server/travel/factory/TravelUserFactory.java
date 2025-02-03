package potatoes.server.travel.factory;

import static potatoes.server.utils.error.ErrorCode.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import potatoes.server.travel.dto.ParticipantResponse;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.entity.TravelUser;
import potatoes.server.travel.repository.TravelUserRepository;
import potatoes.server.user.entity.User;
import potatoes.server.utils.error.exception.WeGoException;

@Component
@RequiredArgsConstructor
public class TravelUserFactory {
	private final TravelUserRepository travelUserRepository;

	public void createOrganizer(Travel travel, User user) {
		TravelUser travelUser = TravelUser.createOrganizer(travel, user);
		travelUserRepository.save(travelUser);
	}

	public void createAttendee(Travel travel, User user) {
		TravelUser travelUser = TravelUser.createAttendee(travel, user);
		travelUserRepository.save(travelUser);
	}

	public TravelUser findTravelUser(Long travelId, Long userId) {
		return travelUserRepository.findByTravelIdAndUserId(travelId, userId)
			.orElseThrow(() -> new WeGoException(TRAVEL_NOT_FOUND));
	}

	public long countByTravel(Travel travel) {
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

	public void deleteTravelUser(TravelUser travelUser) {
		travelUserRepository.delete(travelUser);
	}

}
