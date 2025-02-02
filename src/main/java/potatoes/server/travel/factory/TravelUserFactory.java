package potatoes.server.travel.factory;

import static potatoes.server.utils.error.ErrorCode.*;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
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

	public TravelUser findTravelUser(Long travelId, Long userId) {
		return travelUserRepository.findByTravelIdAndUserId(travelId, userId)
			.orElseThrow(() -> new WeGoException(TRAVEL_NOT_FOUND));
	}
}
