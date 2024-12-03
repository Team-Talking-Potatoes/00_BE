package potatoes.server.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import potatoes.server.config.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Travel extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "travel_mate_count", nullable = false)
	private int travelMateCount;

	@Column(name = "hash_tags", nullable = false)
	private String hashTags;

	@Column(name = "is_domestic", nullable = false)
	boolean isDomestic;

	@Column(name = "travel_location", nullable = false)
	private String travelLocation;

	@Column(name = "departure_location", nullable = false)
	private String departureLocation;

	@Column(name = "start_at", nullable = false)
	private Instant startAt;

	@Column(name = "end_at", nullable = false)
	private Instant endAt;

	@Column(name = "trip_duration", nullable = false)
	private int tripDuration;
}
