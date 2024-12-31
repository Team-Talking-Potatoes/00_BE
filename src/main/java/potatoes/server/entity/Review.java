package potatoes.server.entity;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import potatoes.server.config.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Review extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "travel_id")
	private Travel travel;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "commenter_id")
	private User commenter;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "comment", nullable = false)
	private String comment;

	@Column(name = "star_rating", nullable = false)
	private float starRating;

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
	private List<ReviewImage> reviewImages;

	@Column(name = "organizer_review_tags", nullable = false)
	private String organizerReviewTags;

	@Builder
	public Review(Travel travel, User commenter, String title, String comment, float starRating,
		String organizerReviewTags,
		List<ReviewImage> reviewImages
	) {
		this.travel = travel;
		this.commenter = commenter;
		this.title = title;
		this.comment = comment;
		this.starRating = starRating;
		this.organizerReviewTags = organizerReviewTags;
		this.reviewImages = reviewImages;
	}
}
