package potatoes.server.travel.domain.command;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import potatoes.server.infra.s3.S3UtilsProvider;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.entity.TravelPlan;
import potatoes.server.travel.model.TravelModel;
import potatoes.server.travel.model.TravelPlanModel;
import potatoes.server.travel.repository.TravelPlanRepository;
import potatoes.server.travel.repository.TravelRepository;

@RequiredArgsConstructor
@Component
public class TravelCommander {

	private final TravelRepository travelRepository;
	private final TravelPlanRepository travelPlanRepository;
	private final S3UtilsProvider s3;

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

	public void deleteTravel(Travel travel) {
		travelRepository.delete(travel);
	}
}
