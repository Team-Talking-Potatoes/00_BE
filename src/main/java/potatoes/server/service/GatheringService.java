package potatoes.server.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateGatheringRequest;
import potatoes.server.dto.CreateGatheringResponse;
import potatoes.server.dto.GetGatheringRequest;
import potatoes.server.dto.GetGatheringResponse;
import potatoes.server.entity.Gathering;
import potatoes.server.entity.User;
import potatoes.server.repository.GatheringRepository;
import potatoes.server.utils.Pagination.PageableFactory;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GatheringService {

	private final GatheringRepository gatheringRepository;
	private final PageableFactory pageableFactory;

	public List<GetGatheringResponse> getGatherings(GetGatheringRequest request, Pageable pageable) {
		return gatheringRepository.findGatherings(
				request.id(),
				request.type(),
				request.location(),
				request.getStartOfDay(),
				request.getEndOfDay(),
				request.createdBy(),
				pageable
			).stream()
			.map(GetGatheringResponse::from)
			.collect(Collectors.toList());
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
