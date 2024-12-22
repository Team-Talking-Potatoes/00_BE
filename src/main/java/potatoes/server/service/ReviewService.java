package potatoes.server.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateReviewRequest;
import potatoes.server.dto.GetMyReviewResponse;
import potatoes.server.dto.ReviewPageResponse;
import potatoes.server.dto.SimpleReviewResponse;
import potatoes.server.entity.Review;
import potatoes.server.entity.ReviewImage;
import potatoes.server.entity.Travel;
import potatoes.server.entity.User;
import potatoes.server.error.exception.TravelNotFound;
import potatoes.server.error.exception.UserNotFound;
import potatoes.server.repository.ReviewRepository;
import potatoes.server.repository.TravelRepository;
import potatoes.server.repository.UserRepository;
import potatoes.server.utils.s3.S3UtilsProvider;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final TravelRepository travelRepository;
	private final S3UtilsProvider s3;

	@Transactional
	public void createReview(CreateReviewRequest request, Long userId) {

		User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
		Travel travel = travelRepository.findById(request.travelId()).orElseThrow(TravelNotFound::new);
		Review review = Review.builder()
			.travel(travel)
			.commenter(user)
			.title(request.title())
			.comment(request.comment())
			.starRating(request.starRating())
			.reviewImages(new ArrayList<>())
			.build();

		List<String> fileNames = s3.uploadFiles(request.images());
		List<String> imageUrls = fileNames.stream()
			.map(s3::getFileUrl)
			.toList();

		List<ReviewImage> reviewImages = imageUrls.stream()
			.map(url -> ReviewImage.builder()
				.review(review)
				.imageUrl(url)
				.build())
			.toList();

		review.getReviewImages().addAll(reviewImages);
		reviewRepository.save(review);
	}

	public List<SimpleReviewResponse> getSimpleReviews() {
		Pageable topFive = PageRequest.of(0, 5);
		return reviewRepository.findRecentReviews(topFive).stream()
			.map(review -> new SimpleReviewResponse(review.getId(), review.getCommenter().getNickname(),
				review.getReviewImages().getFirst().getImageUrl()))
			.toList();
	}

	public ReviewPageResponse getMyReviews(int page, int size, Long userId) {
		PageRequest request = PageRequest.of(page, size);
		Page<GetMyReviewResponse> findReviews = reviewRepository.findMyReviews(request, userId);
		return ReviewPageResponse.from(findReviews);
	}
}
