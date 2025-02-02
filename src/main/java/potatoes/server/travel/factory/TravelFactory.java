package potatoes.server.travel.factory;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import potatoes.server.chat.entity.Chat;
import potatoes.server.chat.repository.ChatRepository;
import potatoes.server.infra.s3.S3UtilsProvider;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.entity.TravelPlan;
import potatoes.server.travel.entity.TravelUser;
import potatoes.server.travel.model.TravelModel;
import potatoes.server.travel.model.TravelPlanModel;
import potatoes.server.travel.repository.TravelPlanRepository;
import potatoes.server.travel.repository.TravelRepository;
import potatoes.server.travel.repository.TravelUserRepository;
import potatoes.server.user.entity.User;

@Component
@RequiredArgsConstructor
public class TravelFactory {
	private final S3UtilsProvider s3;
	private final TravelRepository travelRepository;
	private final TravelPlanRepository travelPlanRepository;
	private final TravelUserRepository travelUserRepository;
	private final ChatRepository chatRepository;

	public Travel createTravel(TravelModel request, MultipartFile image) {
		String imageUrl = s3.uploadAndGetUrl(image);
		Travel travel = Travel.from(request, imageUrl);
		return travelRepository.save(travel);
	}

	public void createTravelPlans(List<TravelPlanModel> requests) {
		List<TravelPlan> travelPlans = requests.stream()
			.map(this::createTravelPlan)
			.toList();

		travelPlanRepository.saveAll(travelPlans);
	}

	private TravelPlan createTravelPlan(TravelPlanModel request) {
		String imageUrl = s3.uploadAndGetUrl(request.image());
		return TravelPlan.from(request, imageUrl);
	}

	public void createOrganizer(Travel travel, User user) {
		TravelUser travelUser = TravelUser.createOrganizer(travel, user);
		travelUserRepository.save(travelUser);
	}

	public void createChat(Travel travel, User host) {
		Chat chat = Chat.createTravelChat(travel, host);
		chatRepository.save(chat);
	}
}
