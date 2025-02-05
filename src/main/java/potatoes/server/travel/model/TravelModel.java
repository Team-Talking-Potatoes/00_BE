package potatoes.server.travel.model;

import java.time.Instant;

public record TravelModel(
	String name,
	String description,
	String image,
	int expectedTripCost,
	int minTravelMateCount,
	int maxTravelMateCount,
	String hashTags,
	boolean isDomestic,
	String travelLocation,
	String departureLocation,
	Instant startAt,
	Instant endAt,
	Instant registrationEnd,
	int tripDuration
) {
}
