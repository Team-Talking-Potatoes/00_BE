package potatoes.server.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class UserGatheringId implements Serializable {

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "gathering_id")
	private Long gatheringId;
}
