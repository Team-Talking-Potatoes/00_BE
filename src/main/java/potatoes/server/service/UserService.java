package potatoes.server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.ResetPasswordRequest;
import potatoes.server.entity.User;
import potatoes.server.error.exception.PasswordMismatch;
import potatoes.server.error.exception.UserNotFound;
import potatoes.server.repository.UserRepository;
import potatoes.server.utils.crypto.PasswordEncoder;
import potatoes.server.utils.s3.S3UtilsProvider;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final S3UtilsProvider s3;

	@Transactional
	public void resetPassword(ResetPasswordRequest request, Long userId) {
		User getUser = userRepository.findById(userId).orElseThrow(UserNotFound::new);
		boolean isPasswordMatched = passwordEncoder.matches(request.currentPassword(), getUser.getPassword());

		if (!isPasswordMatched) {
			throw new PasswordMismatch();
		}

		String newPassword = passwordEncoder.encrypt(request.newPassword());

		getUser.resetPassword(newPassword);
	}

	@Transactional
	public void updateUserProfile(MultipartFile profileImage, String nickname, String description, Long userId) {
		User getUser = userRepository.findById(userId).orElseThrow(UserNotFound::new);

		String imageUrl = profileImage != null ? uploadAndReturnUrl(profileImage) : getUser.getProfileImage();
		String updatedNickname = nickname != null ? nickname : getUser.getNickname();
		String updatedDescription = description != null ? description : getUser.getDescription();

		getUser.updateProfile(imageUrl, updatedNickname, updatedDescription);
	}

	private String uploadAndReturnUrl(MultipartFile image) {
		String filaName = s3.uploadFile(image);
		return s3.getFileUrl(filaName);
	}
}
