package potatoes.server.entity;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Chat {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "travel_id")
	private Travel travel;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User host;

	@Column(nullable = false)
	private int maxMemberCount;

	@Column(nullable = false)
	private int currentMemberCount;

	@Builder
	public Chat(Travel travel, User host, int maxMemberCount, int currentMemberCount) {
		this.travel = travel;
		this.host = host;
		this.maxMemberCount = maxMemberCount;
		this.currentMemberCount = currentMemberCount;
	}
}
