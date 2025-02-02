package potatoes.server.travel.entity;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;
import static potatoes.server.utils.error.ErrorCode.*;

import java.time.Duration;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import potatoes.server.config.BaseTimeEntity;
import potatoes.server.travel.model.TravelModel;
import potatoes.server.utils.error.exception.WeGoException;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Travel extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "image", nullable = false)
	private String image;

	@Column(name = "expected_trip_cost", nullable = false)
	private int expectedTripCost;

	@Column(name = "min_travel_mate_count", nullable = false)
	private int minTravelMateCount;

	@Column(name = "max_travel_mate_count", nullable = false)
	private int maxTravelMateCount;

	@Column(name = "hash_tags", nullable = false)
	private String hashTags;

	@Column(name = "is_domestic", nullable = false)
	private boolean isDomestic;

	@Column(name = "travel_location", nullable = false)
	private String travelLocation;

	@Column(name = "departure_location")
	private String departureLocation;

	@Column(name = "start_at", nullable = false)
	private Instant startAt;

	@Column(name = "end_at", nullable = false)
	private Instant endAt;

	@Column(name = "registration_end", nullable = false)
	private Instant registrationEnd;

	@Column(name = "trip_duration", nullable = false)
	private int tripDuration;

	@Builder
	private Travel(String name, String description, String image,
		int expectedTripCost, int minTravelMateCount, int maxTravelMateCount,
		String hashTags, boolean isDomestic, String travelLocation,
		String departureLocation, Instant startAt, Instant endAt,
		Instant registrationEnd, int tripDuration) {
		this.name = name;
		this.description = description;
		this.image = image;
		this.expectedTripCost = expectedTripCost;
		this.minTravelMateCount = minTravelMateCount;
		this.maxTravelMateCount = maxTravelMateCount;
		this.hashTags = hashTags;
		this.isDomestic = isDomestic;
		this.travelLocation = travelLocation;
		this.departureLocation = departureLocation;
		this.startAt = startAt;
		this.endAt = endAt;
		this.registrationEnd = registrationEnd;
		this.tripDuration = tripDuration;
	}

	public static Travel from(TravelModel model, String imageUrl) {
		validateAll(model);

		return Travel.builder()
			.name(model.name())
			.description(model.description())
			.image(imageUrl)
			.expectedTripCost(model.expectedTripCost())
			.minTravelMateCount(model.minTravelMateCount())
			.maxTravelMateCount(model.maxTravelMateCount())
			.hashTags(model.hashTags())
			.isDomestic(model.isDomestic())
			.travelLocation(model.travelLocation())
			.departureLocation(model.departureLocation())
			.startAt(model.startAt())
			.endAt(model.endAt())
			.registrationEnd(model.registrationEnd())
			.tripDuration(model.tripDuration())
			.build();
	}

	private static void validateAll(TravelModel model) {
		validateTravelMateCount(model.minTravelMateCount(), model.maxTravelMateCount());
		validateTravelPeriod(model.startAt(), model.endAt());
		validateHashTags(model.hashTags());
	}

	private static void validateTravelMateCount(int minCount, int maxCount) {
		if (minCount > maxCount) {
			throw new WeGoException(INVALID_TRAVEL_MATE_COUNT);
		}
	}

	private static void validateTravelPeriod(Instant startAt, Instant endAt) {
		if (startAt.isAfter(endAt)) {
			throw new WeGoException(INVALID_TRAVEL_DATE);
		}

		long tripDuration = Duration.between(startAt, endAt).toDays() + 1;
		if (tripDuration < 0) {
			throw new WeGoException(INVALID_TRAVEL_DATE);
		}
	}

	private static void validateHashTags(String hashTags) {
		if (hashTags != null && hashTags.split("#").length > 5) {
			throw new WeGoException(INVALID_TRAVEL_HASHTAGS_VALUE);
		}
	}
}
