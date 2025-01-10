package potatoes.server.travel.entity;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import potatoes.server.config.BaseTimeEntity;

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
	public Travel(String name, String description, String image, int expectedTripCost, int minTravelMateCount,
		int maxTravelMateCount, String hashTags, boolean isDomestic, String travelLocation, String departureLocation,
		Instant startAt, Instant endAt, Instant registrationEnd, int tripDuration) {
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
}
