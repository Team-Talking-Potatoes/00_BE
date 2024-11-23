package potatoes.server.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserGathering {

	@EmbeddedId
	@Column(name = "user_gathering_id", nullable = false)
	private UserGatheringId userGatheringId;

	@Column(name = "joined_at", nullable = false)
	private Instant joinedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gathering_id")
	private Gathering gathering;
}
