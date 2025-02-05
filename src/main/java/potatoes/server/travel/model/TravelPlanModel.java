package potatoes.server.travel.model;

import org.springframework.web.multipart.MultipartFile;

import potatoes.server.travel.entity.Travel;

public record TravelPlanModel(
	Travel travel,
	int tripDay,
	int tripOrderNumber,
	String destination,
	MultipartFile image,
	String description

) {
}
