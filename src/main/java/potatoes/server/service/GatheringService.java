package potatoes.server.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateGatheringRequest;
import potatoes.server.dto.CreateGatheringResponse;
import potatoes.server.dto.GetGatheringParticipantRequest;
import potatoes.server.dto.GetGatheringParticipantResponse;
import potatoes.server.dto.GetGatheringRequest;
import potatoes.server.dto.GetGatheringResponse;
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

	public List<GetGatheringParticipantResponse> getGatheringParticipant(GetGatheringParticipantRequest request,
		Pageable pageable) {
		return userGatheringRepository.findParticipants(request.getGatheringId(), pageable)
			.getContent()
			.stream()
			.map(GetGatheringParticipantResponse::of)
			.toList();
	}

	@Transactional
	public CreateGatheringResponse integrateGatheringCreation(
		CreateGatheringRequest req,
		MultipartFile multipartFile,
		Long memberId
	) {
		String imageUrl = uploadGatheringImage(multipartFile);
		Gathering gathering = createGathering(req, imageUrl, memberId);

		gatheringRepository.save(gathering);

		return CreateGatheringResponse.from(gathering);
	}

	private Gathering createGathering(CreateGatheringRequest req, String image, Long memberId) {
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
}
