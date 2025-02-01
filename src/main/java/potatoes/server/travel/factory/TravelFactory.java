package potatoes.server.travel.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import potatoes.server.chat.entity.Chat;
import potatoes.server.infra.s3.S3UtilsProvider;
import potatoes.server.travel.dto.CreateTravelRequest;
import potatoes.server.travel.dto.DetailTravelRequest;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.entity.TravelPlan;
import potatoes.server.travel.entity.TravelUser;
import potatoes.server.user.entity.User;

@Component
@RequiredArgsConstructor
public class TravelFactory {
	private final S3UtilsProvider s3;

	public Travel createTravel(CreateTravelRequest request) {
		String imageUrl = s3.uploadAndGetUrl(request.travelImage());
		return Travel.create(request, imageUrl);
	}

	public List<TravelPlan> createTravelPlans(CreateTravelRequest request, Travel travel) {
		return request.detailTravel().stream()
			.map(details -> createTravelPlan(details, travel))
			.toList();
	}

	private TravelPlan createTravelPlan(DetailTravelRequest details, Travel travel) {
		String imageUrl = s3.uploadAndGetUrl(details.destinationImage());
		return TravelPlan.create(details, travel, imageUrl);
	}

	public TravelUser createParticipant(User user, Travel travel) {
		return TravelUser.createOrganizer(user, travel);
	}

	public Chat createChat(Travel travel, User host) {
		return Chat.createTravelChat(travel, host);
	}
}
