package potatoes.server.chat.entity;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import potatoes.server.travel.entity.Travel;
import potatoes.server.user.entity.User;

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

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User host;

	@Column(nullable = false)
	private int maxMemberCount;

	@Column(nullable = false)
	private int currentMemberCount;

	@Builder
	public Chat(String name, Travel travel, User host, int maxMemberCount, int currentMemberCount) {
		this.name = name;
		this.travel = travel;
		this.host = host;
		this.maxMemberCount = maxMemberCount;
		this.currentMemberCount = currentMemberCount;
	}

	public static Chat createTravelChat(Travel travel, User host) {
		return Chat.builder()
			.name(travel.getName())
			.host(host)
			.travel(travel)
			.currentMemberCount(1)
			.maxMemberCount(travel.getMaxTravelMateCount())
			.build();
	}

	public void newMemberJoined() {
		currentMemberCount++;
	}

	public void memberLeaved() {
		currentMemberCount--;
	}
}
