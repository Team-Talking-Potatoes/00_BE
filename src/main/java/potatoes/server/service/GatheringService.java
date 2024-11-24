package potatoes.server.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateGatheringResponse;
import potatoes.server.entity.Gathering;
import potatoes.server.repository.GatheringRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GatheringService {

	private final GatheringRepository gatheringRepository;

	@Transactional
	public void createGathering(MultipartFile multipartFile, CreateGatheringResponse response) {
		Gathering gathering = Gathering.builder()
			.type(response.type())
			.name(response.name())
			.dateTime(response.dateTime())
			.registrationEnd(response.registrationEnd())
			.location(response.location())
			.capacity(response.capacity())
			// S3 붙여서 이미지 url 뱉어야함
			.image(multipartFile)
		// TODO 유저 조회 기능으로 넣을 예정
		// jwt토큰 로직 생서해야함
		// .user(response.)

	}
}
