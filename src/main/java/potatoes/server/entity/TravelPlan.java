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
import lombok.Builder;
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

	@Column(name = "trip_order_number", nullable = false)
	private int tripOrderNumber;

	@Column(name = "destination", nullable = false)
	private String destination;

	@Column(name = "image", nullable = false)
	private String image;

	@Column(name = "description", nullable = false)
	private String description;

	@Builder
	public TravelPlan(Travel travel, int tripDay, int tripOrderNumber, String destination, String image,
		String description) {
		this.travel = travel;
		this.tripDay = tripDay;
		this.tripOrderNumber = tripOrderNumber;
		this.destination = destination;
		this.image = image;
		this.description = description;
	}
}
