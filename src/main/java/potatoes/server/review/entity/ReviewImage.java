package potatoes.server.review.entity;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import potatoes.server.config.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class ReviewImage extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "review_id")
	private Review review;

	@Column(nullable = false)
	private String imageUrl;

	@Builder
	public ReviewImage(Review review, String imageUrl) {
		this.review = review;
		this.imageUrl = imageUrl;
	}
}
