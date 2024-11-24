package potatoes.server.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import potatoes.server.config.BaseTimeEntity;
import potatoes.server.constant.GatheringType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Gathering extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "gathering_id")
	private Long id;

	@Column(name = "gathering_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private GatheringType type;

	@Column(name = "name")
	private String name;

	@Column(name = "date_time", nullable = false)
	private Instant dateTime;

	@Column(name = "registration_end", nullable = false)
	private Instant registrationEnd;

	@Column(name = "location", nullable = false)
	private String location;

	@Column(name = "participant_count", nullable = false)
	private int participantCount = 0;

	@Column(name = "capacity", nullable = false)
	private int capacity;

	@Column(name = "image")
	private String image;

	@Column(name = "created_by", nullable = false)
	private Long createdBy;

	@Column(name = "canceled_at")
	private Instant canceledAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public Gathering(GatheringType type, String name, Instant dateTime, Instant registrationEnd, String location,
		int capacity, String image, Instant canceledAt, User user) {
		this.type = type;
		this.name = name;
		this.dateTime = dateTime;
		this.registrationEnd = registrationEnd;
		this.location = location;
		this.participantCount = 1;
		this.capacity = capacity;
		this.image = image;
		this.createdBy = user.getId();
		this.canceledAt = canceledAt;
		this.user = user;
	}
}
