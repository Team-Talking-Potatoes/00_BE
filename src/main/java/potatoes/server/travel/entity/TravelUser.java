package potatoes.server.travel.entity;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import potatoes.server.config.BaseTimeEntity;
import potatoes.server.user.entity.User;
import potatoes.server.utils.constant.ParticipantRole;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class TravelUser extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "role", nullable = false)
	@Enumerated(value = EnumType.STRING)
	private ParticipantRole role;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "travel_id")
	private Travel travel;

	@Builder
	public TravelUser(ParticipantRole role, User user, Travel travel) {
		this.role = role;
		this.user = user;
		this.travel = travel;
	}

	public static TravelUser createOrganizer(Travel travel, User user) {
		return TravelUser.builder()
			.role(ParticipantRole.ORGANIZER)
			.travel(travel)
			.user(user)
			.build();
	}
}
