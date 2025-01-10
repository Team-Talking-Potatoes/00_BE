package potatoes.server.dto;

import potatoes.server.travel.entity.TravelPlan;

public record TravelPlanResponse(
	int tripDay,
	int tripOrderNumber,
	String destination,
	String description,
	String image
) {

	public static TravelPlanResponse from(TravelPlan travelPlan) {
		return new TravelPlanResponse(
			travelPlan.getTripDay(),
			travelPlan.getTripOrderNumber(),
			travelPlan.getDestination(),
			travelPlan.getDescription(),
			travelPlan.getImage()
		);
	}
}
