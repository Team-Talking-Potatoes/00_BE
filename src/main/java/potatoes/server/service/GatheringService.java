package potatoes.server.service;

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
import potatoes.server.entity.Gathering;
import potatoes.server.entity.User;
import potatoes.server.repository.GatheringRepository;
import potatoes.server.repository.UserGatheringRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GatheringService {

	private final GatheringRepository gatheringRepository;
	private final UserGatheringRepository userGatheringRepository;

	public List<GetGatheringResponse> getGatherings(GetGatheringRequest request, Pageable pageable) {
		return gatheringRepository.findGatheringsWithFilters(
				request.ids(),
				request.type(),
				request.location(),
				request.date(),
				request.createdBy(),
				pageable
			)
			.map(GetGatheringResponse::from)
			.getContent();
	}

	public GetDetailedGatheringResponse getDetailedGathering(Long gatheringId) {
		Gathering gathering = gatheringRepository.findById(gatheringId)
			.orElseThrow(() -> new RuntimeException("Gathering not found with id: " + gatheringId));

		return GetDetailedGatheringResponse.from(gathering);
	}

	public List<GetGatheringParticipantResponse> getGatheringParticipant(
		GetGatheringParticipantRequest request, Pageable pageable) {
		return userGatheringRepository.findParticipants(request.gatheringId(), pageable)
			.getContent()
			.stream()
			.map(GetGatheringParticipantResponse::from)
			.toList();
	}

	@Transactional
	public CreateGatheringResponse integrateGatheringCreation(
		CreateGatheringRequest req,
		MultipartFile multipartFile,
		Long userId
	) {
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
	public PutGatheringResponse putGathering(Long userId, Long gatheringId) {
		int updatedCount = gatheringRepository.cancelGathering(gatheringId, userId, Instant.now());
		if (updatedCount == 0) {
			throw new RuntimeException("모임 취소 권한이 없습니다.");
		}
		Gathering updatedGathering = gatheringRepository.findById(gatheringId).get();

		return PutGatheringResponse.from(updatedGathering);
	}
}
