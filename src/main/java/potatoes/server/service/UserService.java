package potatoes.server.service;

import static potatoes.server.error.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.DeleteUserRequest;
import potatoes.server.dto.GetUserProfileResponse;
import potatoes.server.dto.PopularUserResponse;
import potatoes.server.dto.ResetPasswordRequest;
import potatoes.server.entity.TravelUser;
import potatoes.server.entity.User;
import potatoes.server.error.exception.WeGoException;
import potatoes.server.repository.ReviewRepository;
import potatoes.server.repository.TravelUserRepository;
import potatoes.server.repository.UserRepository;
import potatoes.server.utils.crypto.PasswordEncoder;
import potatoes.server.utils.s3.S3UtilsProvider;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
	private final UserRepository userRepository;
	private final TravelUserRepository travelUserRepository;
	private final ReviewRepository reviewRepository;
	private final PasswordEncoder passwordEncoder;
	private final S3UtilsProvider s3;

	public GetUserProfileResponse getUserProfile(Long userId) {
		User getUser = userRepository.findById(userId).orElseThrow(() -> new WeGoException(USER_NOT_FOUND));

		return new GetUserProfileResponse(
			getUser.getEmail(),
			getUser.getNickname(),
			getUser.getProfileImage(),
			getUser.getDescription()
		);
	}

	@Transactional
	public void resetPassword(ResetPasswordRequest request, Long userId) {
		User getUser = userRepository.findById(userId).orElseThrow(() -> new WeGoException(USER_NOT_FOUND));
		validatePassword(request.currentPassword(), getUser.getPassword());

		String newPassword = passwordEncoder.encrypt(request.newPassword());

		getUser.resetPassword(newPassword);
	}

	@Transactional
	public void updateUserProfile(MultipartFile profileImage, String nickname, String description, Long userId) {
		User getUser = userRepository.findById(userId).orElseThrow(() -> new WeGoException(USER_NOT_FOUND));

		String imageUrl = profileImage != null ? uploadAndReturnUrl(profileImage) : getUser.getProfileImage();
		String updatedNickname = nickname != null ? nickname : getUser.getNickname();
		String updatedDescription = description != null ? description : getUser.getDescription();

		getUser.updateProfile(imageUrl, updatedNickname, updatedDescription);
	}

	private String uploadAndReturnUrl(MultipartFile image) {
		String filaName = s3.uploadFile(image);
		return s3.getFileUrl(filaName);
	}

	@Transactional
	public void deleteUser(DeleteUserRequest request, Long userId) {
		User getUser = userRepository.findById(userId).orElseThrow(() -> new WeGoException(USER_NOT_FOUND));
		validatePassword(request.password(), getUser.getPassword());
		userRepository.delete(getUser);
	}

	private void validatePassword(String rawPassword, String encrpytedPassword) {
		boolean isPasswordMatched = passwordEncoder.matches(rawPassword, encrpytedPassword);

		if (!isPasswordMatched) {
			throw new WeGoException(PASSWORD_MISMATCH);
		}
	}

	public List<PopularUserResponse> findPopularUsers() {
		LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1L);

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

				return new PopularUserResponse(
					user.getProfileImage(),
					user.getNickname(),
					travelUsers.size(),
					totalReviews,
					travelUsers.getFirst().getTravel().getHashTags()
				);
			})
			.filter(Objects::nonNull)
			.toList();
	}

}
