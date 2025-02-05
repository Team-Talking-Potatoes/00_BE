package potatoes.server.travel.domain.command;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.entity.TravelUser;
import potatoes.server.travel.repository.TravelUserRepository;
import potatoes.server.user.entity.User;

@RequiredArgsConstructor
@Component
public class TravelUserCommander {
	private final TravelUserRepository travelUserRepository;

	public void createOrganizer(Travel travel, User user) {
		TravelUser travelUser = TravelUser.createOrganizer(travel, user);
		travelUserRepository.save(travelUser);
	}

	public void createAttendee(Travel travel, User user) {
		TravelUser travelUser = TravelUser.createAttendee(travel, user);
		travelUserRepository.save(travelUser);
	}

	public void deleteTravelUser(TravelUser travelUser) {
		travelUserRepository.delete(travelUser);
	}
}
