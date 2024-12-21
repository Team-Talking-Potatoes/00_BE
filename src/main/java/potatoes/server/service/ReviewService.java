package potatoes.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateReviewRequest;
import potatoes.server.dto.GetDetailsReview;
import potatoes.server.dto.GetMyReviewResponse;
import potatoes.server.dto.ReviewPageResponse;
import potatoes.server.entity.Review;
import potatoes.server.entity.ReviewImage;
import potatoes.server.entity.ReviewLike;
import potatoes.server.entity.Travel;
import potatoes.server.entity.User;
import potatoes.server.error.exception.ReviewLikeAlreadyExist;
import potatoes.server.error.exception.ReviewLikeNotFound;
import potatoes.server.error.exception.ReviewNotFound;
import potatoes.server.error.exception.TravelNotFound;
import potatoes.server.error.exception.UserNotFound;
import potatoes.server.repository.ReviewLikeRepository;
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
	private final ReviewLikeRepository reviewLikeRepository;
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

	public GetDetailsReview getDetailsReview(Long reviewId) {
		int reviewLikes = reviewLikeRepository.countAllByReviewId(reviewId);
		Review review = reviewRepository.findReviewWithImagesAndCommenter(reviewId);

		return new GetDetailsReview(
			review.getId(),
			review.getTitle(),
			review.getComment(),
			review.getStarRating(),
			review.getReviewImages().stream()
				.map(ReviewImage::getImageUrl)
				.collect(Collectors.toList()),
			review.getCommenter().getNickname(),
			reviewLikes,
			review.getTravel().getTravelLocation(),
			review.getCreatedAt()
		);
	}

	@Transactional
	public void addReviewLike(Long reviewId, Long userId) {

		if (reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId)) {
			throw new ReviewLikeAlreadyExist();
		}

		Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFound::new);
		User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);

		ReviewLike reviewLike = ReviewLike.builder()
			.review(review)
			.user(user)
			.build();

		reviewLikeRepository.save(reviewLike);
	}

	@Transactional
	public void removeReviewLike(Long reviewId, Long userId) {
		ReviewLike reviewLike = reviewLikeRepository.findByUserIdAndReviewId(reviewId, userId).orElseThrow(
			ReviewLikeNotFound::new
		);

		reviewLikeRepository.delete(reviewLike);
	}

	public ReviewPageResponse getMyReviews(int page, int size, Long userId) {
		PageRequest request = PageRequest.of(page, size);
		Page<GetMyReviewResponse> findReviews = reviewRepository.findMyReviews(request, userId);
		return ReviewPageResponse.from(findReviews);
	}

}
