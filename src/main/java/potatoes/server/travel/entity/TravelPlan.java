package potatoes.server.travel.entity;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;
import static potatoes.server.utils.error.ErrorCode.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import potatoes.server.travel.model.TravelPlanModel;
import potatoes.server.utils.error.exception.WeGoException;

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

	@Column(name = "image")
	private String image;

	@Column(name = "description", nullable = false)
	private String description;

	@Builder
	public TravelPlan(Travel travel, int tripDay, int tripOrderNumber, String destination, String image,
		String description) {
		validateTripDay(tripDay, travel.getTripDuration());

		this.travel = travel;
		this.tripDay = tripDay;
		this.tripOrderNumber = tripOrderNumber;
		this.destination = destination;
		this.image = image;
		this.description = description;
	}

	public static TravelPlan from(TravelPlanModel model, String imageUrl) {
		return TravelPlan.builder()
			.travel(model.travel())
			.image(imageUrl)
			.tripDay(model.tripDay())
			.tripOrderNumber(model.tripOrderNumber())
			.destination(model.destination())
			.description(model.description())
			.build();
	}

	private void validateTripDay(int tripDay, int tripDuration) {
		if (tripDay > tripDuration) {
			throw new WeGoException(INVALID_TRAVEL_DETAIL_INFO);
		}
	}
}
