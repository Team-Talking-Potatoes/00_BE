package potatoes.server.travel.factory;

import static potatoes.server.utils.error.ErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import potatoes.server.chat.entity.Chat;
import potatoes.server.chat.repository.ChatRepository;
import potatoes.server.infra.s3.S3UtilsProvider;
import potatoes.server.travel.dto.TravelPlanResponse;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.entity.TravelPlan;
import potatoes.server.travel.model.TravelModel;
import potatoes.server.travel.model.TravelPlanModel;
import potatoes.server.travel.repository.TravelPlanRepository;
import potatoes.server.travel.repository.TravelRepository;
import potatoes.server.user.entity.User;
import potatoes.server.utils.error.exception.WeGoException;

@Component
@RequiredArgsConstructor
public class TravelFactory {
	private final S3UtilsProvider s3;
	private final TravelRepository travelRepository;
	private final TravelPlanRepository travelPlanRepository;
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

	public void createChat(Travel travel, User host) {
		Chat chat = Chat.createTravelChat(travel, host);
		chatRepository.save(chat);
	}

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

	public void deleteTravel(Travel travel) {
		travelRepository.delete(travel);
	}
}
