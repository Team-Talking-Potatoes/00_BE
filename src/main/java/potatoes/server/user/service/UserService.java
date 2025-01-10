package potatoes.server.user.service;

import static potatoes.server.utils.error.ErrorCode.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import potatoes.server.infra.redis.RedisStore;
import potatoes.server.infra.s3.S3UtilsProvider;
import potatoes.server.review.entity.Review;
import potatoes.server.review.repository.ReviewRepository;
import potatoes.server.travel.entity.TravelUser;
import potatoes.server.travel.repository.TravelUserRepository;
import potatoes.server.user.dto.DeleteUserRequest;
import potatoes.server.user.dto.GetUserProfileResponse;
import potatoes.server.user.dto.PasswordCertification;
import potatoes.server.user.dto.PopularUserResponse;
import potatoes.server.user.dto.ResetPasswordRequest;
import potatoes.server.user.entity.User;
import potatoes.server.user.repository.UserRepository;
import potatoes.server.utils.crypto.PasswordEncoder;
import potatoes.server.utils.error.exception.WeGoException;
import potatoes.server.utils.jwt.JwtTokenUtil;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
	private final UserRepository userRepository;
	private final TravelUserRepository travelUserRepository;
	private final ReviewRepository reviewRepository;
	private final RedisStore redisStore;
	private final PasswordEncoder passwordEncoder;
	private final S3UtilsProvider s3;
	private final JwtTokenUtil jwtTokenUtil;
	private static final Duration EXPIRATION = Duration.ofMinutes(5);
	private static final String DELETE_USER_VERIFY_PREFIX = "USER:DELETE:VERIFY:";

	public GetUserProfileResponse getUserProfile(Long userId) {
		User getUser = getUserByIdAndCheckExist(userId);

		return GetUserProfileResponse.from(getUser);
	}

	@Transactional
	public void resetPassword(ResetPasswordRequest request, Long userId) {
		User getUser = getUserByIdAndCheckExist(userId);
		certificatePassword(request.currentPassword(), getUser.getPassword());

		String newPassword = passwordEncoder.encrypt(request.newPassword());

		getUser.resetPassword(newPassword);
	}

	@Transactional
	public void updateUserProfile(MultipartFile profileImage, String nickname, String description, Long userId) {
		User getUser = getUserByIdAndCheckExist(userId);

		String imageUrl = profileImage != null ? uploadAndReturnUrl(profileImage) : getUser.getProfileImage();
		String updatedNickname = nickname != null ? nickname : getUser.getNickname();
		String updatedDescription = description != null ? description : getUser.getDescription();

		getUser.updateProfile(imageUrl, updatedNickname, updatedDescription);
	}

	@Transactional
	public void deleteUser(PasswordCertification request, Long userId) {
		User getUser = getUserByIdAndCheckExist(userId);
		certificateToken(request, userId);
		userRepository.delete(getUser);
	}

	public PasswordCertification certificateAndReturnToken(DeleteUserRequest request, Long userId) {
		User getUser = getUserByIdAndCheckExist(userId);

		certificatePassword(request.password(), getUser.getPassword());
		String deleteUserToken = createAndStoreToken(userId);

		return new PasswordCertification(deleteUserToken);
	}

	public List<PopularUserResponse> findPopularUsers() {
		Instant oneMonthAgo = LocalDateTime.now()
			.minusMonths(1L)
			.atZone(ZoneId.systemDefault())
			.toInstant();

		return userRepository.findAll().stream()
			.map(user -> {
				List<TravelUser> travelUsers = travelUserRepository.findOrganizersCreatedAfter(oneMonthAgo,
					user.getId());

				if (travelUsers.isEmpty()) {
					return null;
				}

				long totalReviews = travelUsers.stream()
					.mapToLong(tu -> reviewRepository.countByTravelId(tu.getTravel().getId()))
					.sum();

				String hashTags = reviewRepository.findTop3ReviewsByOrganizerIdOrderByCreatedAtDesc(user.getId())
					.stream()
					.map(Review::getOrganizerReviewTags)
					.filter(Objects::nonNull)
					.collect(Collectors.joining());

				return new PopularUserResponse(
					user.getId(),
					user.getProfileImage(),
					user.getNickname(),
					travelUsers.size(),
					totalReviews,
					hashTags.isEmpty() ? null : hashTags
				);
			})
			.filter(Objects::nonNull)
			.toList();
	}

	private String createAndStoreToken(Long userId) {
		String token = jwtTokenUtil.createAccessToken(userId.toString());

		redisStore.store(DELETE_USER_VERIFY_PREFIX + userId, token, EXPIRATION);
		return token;
	}

	private void certificateToken(PasswordCertification request, Long userId) {
		String storedAccessToken = redisStore.find(DELETE_USER_VERIFY_PREFIX + userId);

		if (storedAccessToken == null) {
			throw new WeGoException(VERIFY_NUMBER_EXPIRED);
		}

		if (!storedAccessToken.equals(request.deleteUserToken())) {
			throw new WeGoException(VERIFY_NUMBER_NOT_VALID);
		}

		if (!jwtTokenUtil.validateToken(storedAccessToken)) {
			throw new WeGoException(UNAUTHORIZED);
		}
		redisStore.remove(DELETE_USER_VERIFY_PREFIX + userId);
	}

	private User getUserByIdAndCheckExist(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new WeGoException(USER_NOT_FOUND));
	}

	private void certificatePassword(String rawPassword, String encrpytedPassword) {
		boolean isPasswordMatched = passwordEncoder.matches(rawPassword, encrpytedPassword);

		if (!isPasswordMatched) {
			throw new WeGoException(PASSWORD_MISMATCH);
		}
	}

	private String uploadAndReturnUrl(MultipartFile image) {
		String filaName = s3.uploadFile(image);
		return s3.getFileUrl(filaName);
	}
}
