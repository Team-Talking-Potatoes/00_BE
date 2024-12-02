package potatoes.server.service;

import static potatoes.server.utils.time.DateTimeUtils.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateReviewRequest;
import potatoes.server.dto.CreateReviewResponse;
import potatoes.server.dto.GetReviewRequest;
import potatoes.server.dto.GetReviewResponse;
import potatoes.server.dto.GetReviewScoreInfo;
import potatoes.server.entity.Gathering;
import potatoes.server.entity.Review;
import potatoes.server.entity.User;
import potatoes.server.error.exception.AlreadyJoinedGatheringException;
import potatoes.server.error.exception.GatheringNotFoundException;
import potatoes.server.error.exception.UserNotFoundException;
import potatoes.server.repository.GatheringRepository;
import potatoes.server.repository.ReviewRepository;
import potatoes.server.repository.UserGatheringRepository;
import potatoes.server.repository.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final GatheringRepository gatheringRepository;
	private final UserGatheringRepository userGatheringRepository;
	private final UserRepository userRepository;

	@Transactional
	public CreateReviewResponse createReview(Long userId, CreateReviewRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		Gathering gathering = gatheringRepository.findById(request.gatheringId())
			.orElseThrow(GatheringNotFoundException::new);

		if (!userGatheringRepository.existsByUserIdAndGatheringIdAndCanceledAtIsNull(userId, gathering.getId())) {
			throw new AlreadyJoinedGatheringException();
		}

		Review review = Review.builder()
			.comment(request.comment())
			.user(user)
			.gathering(gathering)
			.score(request.score())
			.build();
		reviewRepository.save(review);

		return CreateReviewResponse.from(gathering, user, review);
	}

	public List<GetReviewResponse> getReviews(GetReviewRequest request, Pageable pageable) {
		return reviewRepository.findReviewWithFilter(
			request.gatheringId(), request.userId(), request.type(), request.location(), getStartOfDay(request.date()),
			getStartOfDay(request.dueDate()), pageable
		).map(GetReviewResponse::from).getContent();
	}

	public List<GetReviewScoreInfo> getReviewScoreAverage(String ids) {
		List<GetReviewScoreInfo> result = new ArrayList<>();
		for (String id : ids.trim().split(",")) {
			List<Review> reviews = reviewRepository.findAllByGathering(Long.valueOf(id));
			if (reviews.isEmpty()) {
				continue;
			}

			int[] scoreCounts = new int[5];
			int totalScore = 0;

			for (Review review : reviews) {
				int score = review.getScore();
				if (score >= 1 && score <= 5) {
					scoreCounts[score - 1]++;
					totalScore += score;
				}
			}

			int average = totalScore / reviews.size();
			result.add(
				GetReviewScoreInfo.from(reviews.getFirst(), average, scoreCounts[0], scoreCounts[1], scoreCounts[2],
					scoreCounts[3], scoreCounts[4]));
		}
		return result;
	}
}
