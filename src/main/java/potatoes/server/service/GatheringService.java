package potatoes.server.service;

import static potatoes.server.utils.time.DateTimeUtils.*;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateGatheringRequest;
import potatoes.server.dto.CreateGatheringResponse;
import potatoes.server.dto.GetDetailedGatheringResponse;
import potatoes.server.dto.GetGatheringParticipantRequest;
import potatoes.server.dto.GetGatheringParticipantResponse;
import potatoes.server.dto.GetGatheringRequest;
import potatoes.server.dto.GetGatheringResponse;
import potatoes.server.dto.PutGatheringResponse;
import potatoes.server.dto.SuccessResponse;
import potatoes.server.entity.Gathering;
import potatoes.server.entity.User;
import potatoes.server.entity.UserGathering;
import potatoes.server.error.exception.AlreadyJoinedGatheringException;
import potatoes.server.error.exception.GatheringNotFoundException;
import potatoes.server.error.exception.NotParticipatingGatheringException;
import potatoes.server.error.exception.PastGatheringException;
import potatoes.server.error.exception.UnauthorizedGatheringCancelException;
import potatoes.server.repository.GatheringRepository;
import potatoes.server.repository.UserGatheringRepository;
import potatoes.server.repository.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GatheringService {
	private final GatheringRepository gatheringRepository;
	private final UserGatheringRepository userGatheringRepository;
	private final UserRepository userRepository;

	@Transactional
	public CreateGatheringResponse integrateGatheringCreation(CreateGatheringRequest request,
		MultipartFile multipartFile,
		Long userId) {
		String imageUrl = uploadGatheringImage(multipartFile);
		Gathering gathering = createGathering(request, imageUrl, userId);

		gatheringRepository.save(gathering);

		return CreateGatheringResponse.from(gathering);
	}

	private Gathering createGathering(CreateGatheringRequest request, String image, Long userId) {
		return Gathering.builder()
			.type(request.type())
			.name(request.name())
			.dateTime(request.dateTime())
			.registrationEnd(request.registrationEnd())
			.location(request.location())
			.capacity(request.capacity())
			.image(image)
			.user(findByUser(userId))
			.build();
	}

	private String uploadGatheringImage(MultipartFile multipartFile) {
		return "1";
		// TODO 멀티파트 image로 변경하는 S3 로직 추가
	}

	@Transactional
	public SuccessResponse joinGathering(Long userId, Long gatheringId) {
		Gathering gathering = findNotCanceledGathering(gatheringId);

		if (userGatheringRepository.existsByUserIdAndGatheringIdAndCanceledAtIsNull(userId, gatheringId)) {
			throw new AlreadyJoinedGatheringException();
		}

		gathering.increaseParticipantCount();

		UserGathering userGathering = UserGathering.builder()
			.user(User.builder().build())
			.gathering(gathering)
			.joinedAt(Instant.now())
			.build();

		// TODO 유저 정보 조회 추후 추가

		userGatheringRepository.save(userGathering);

		return new SuccessResponse("모임에 참여했습니다");
	}

	public List<GetGatheringResponse> getGatherings(
		GetGatheringRequest request,
		Pageable pageable
	) {
		return gatheringRepository.findGatheringsWithFilters(
			request.ids(),
			request.type(),
			request.location(),
			getStartOfDay(request.date()),
			getEndOfDay(request.date()),
			request.createdBy(),
			pageable
		).map(GetGatheringResponse::from).getContent();
	}

	public GetDetailedGatheringResponse getDetailedGathering(Long gatheringId) {
		Gathering gathering = findNotCanceledGathering(gatheringId);

		return GetDetailedGatheringResponse.from(gathering);
	}

	public List<GetGatheringParticipantResponse> getGatheringParticipants(GetGatheringParticipantRequest request,
		Pageable pageable) {
		return userGatheringRepository.findParticipants(request.gatheringId(), pageable)
			.getContent()
			.stream()
			.map(GetGatheringParticipantResponse::from)
			.toList();
	}

	@Transactional
	public PutGatheringResponse cancelGathering(Long userId, Long gatheringId) {
		Gathering gathering = findNotCanceledGathering(gatheringId);

		if (!gathering.getCreatedBy().equals(userId)) {
			throw new UnauthorizedGatheringCancelException();
		}

		gathering.cancel();

		userGatheringRepository.findAllByGatheringIdAndCanceledAtIsNull(gatheringId)
			.forEach(UserGathering::cancel);

		return PutGatheringResponse.from(gathering);
	}

	@Transactional
	public SuccessResponse cancelGatheringParticipation(Long userId, Long gatheringId) {
		Gathering gathering = findNotCanceledGathering(gatheringId);

		if (gathering.getDateTime().isBefore(Instant.now())) {
			throw new PastGatheringException();
		}

		UserGathering userGathering = userGatheringRepository.findByUserIdAndGatheringIdAndCanceledAtIsNull(userId,
			gatheringId).orElseThrow(NotParticipatingGatheringException::new);

		gathering.decreaseParticipantCount();

		userGathering.cancel();

		return new SuccessResponse("모임을 참여 취소했습니다.");
	}

	private Gathering findNotCanceledGathering(Long gatheringId) {
		return gatheringRepository.findByIdAndCanceledAtIsNull(gatheringId).orElseThrow(
			GatheringNotFoundException::new
		);
	}

	private User findByUser(Long userId){
		return userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);
	}

}
