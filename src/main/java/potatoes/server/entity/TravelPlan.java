package potatoes.server.entity;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class TravelPlan {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "travel_id")
	private Travel travel;

	@Column(name = "trip_day", nullable = false)
	private int tripDay;

	@Column(name = "destination", nullable = false)
	private String destination;

	@Column(name = "image", nullable = false)
	private String image;

	@Column(name = "description", nullable = false)
	private String description;
}
