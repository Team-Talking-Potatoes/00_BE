package potatoes.server.travel.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.model.TravelPlanModel;

public record DetailTravelRequest(

	@NotNull(message = "상세일정 날을 입력해주세요.")
	@Positive(message = "올바른 값을 입력해주세요.")
	Integer tripDay,

	@NotNull(message = "몇번째 일정인지 입력해주세요.")
	@Positive(message = "올바른 값을 입력해주세요.")
	Integer tripOrderNumber,

	@NotNull(message = "상세 일정 목적지 입력해주세요.")
	String destination,

	@NotNull(message = "상세 일정 설명을 입력해주세요.")
	String description,

	MultipartFile destinationImage
) {
	public static List<TravelPlanModel> toModels(List<DetailTravelRequest> requests, Travel travel) {
		return requests.stream()
			.map(request -> new TravelPlanModel(
				travel,
				request.tripDay,
				request.tripOrderNumber,
				request.destination,
				null,
				request.description
			))
			.collect(Collectors.toList());
	}
}
