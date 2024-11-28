package potatoes.server.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

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
import potatoes.server.repository.GatheringRepository;
import potatoes.server.repository.UserGatheringRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GatheringService {
	private final GatheringRepository gatheringRepository;
	private final UserGatheringRepository userGatheringRepository;

	@Transactional
	public CreateGatheringResponse integrateGatheringCreation(CreateGatheringRequest req, MultipartFile multipartFile,
		Long userId) {
		String imageUrl = uploadGatheringImage(multipartFile);
		Gathering gathering = createGathering(req, imageUrl, userId);

		gatheringRepository.save(gathering);

		return CreateGatheringResponse.from(gathering);
	}

	private Gathering createGathering(CreateGatheringRequest req, String image, Long userId) {
		return Gathering.builder()
			.type(req.type())
			.name(req.name())
			.dateTime(req.dateTime())
			.registrationEnd(req.registrationEnd())
			.location(req.location())
			.capacity(req.capacity())
			.image(image)
			.user(User.builder().build())
			// TODO 유저 조회 기능으로 넣을 예정
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
			throw new RuntimeException("이미 참여한 모임입니다.");
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
		return gatheringRepository.findGatheringsWithFilters(request.ids(), request.type(), request.location(),
			request.date(), request.createdBy(), pageable).map(GetGatheringResponse::from).getContent();
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
			throw new RuntimeException("모임 취소 권한이 없습니다.");
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
			throw new RuntimeException("이미 지난 모임은 참여 취소가 불가능합니다.");
		}

		UserGathering userGathering = userGatheringRepository.findByUserIdAndGatheringIdAndCanceledAtIsNull(userId,
			gatheringId).orElseThrow(() -> new RuntimeException("참여하지 않은 모임입니다."));

		gathering.decreaseParticipantCount();

		userGathering.cancel();

		return new SuccessResponse("모임을 참여 취소했습니다.");
	}

	private Gathering findNotCanceledGathering(Long gatheringId) {
		Gathering gathering = gatheringRepository.findByIdAndCanceledAtIsNull(gatheringId);

		if (gathering == null) {
			throw new NotFoundException("존재하지 않는 모임이거나 이미 취소된 모임입니다.");
		}

		return gathering;
	}
}
