package potatoes.server.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.webjars.NotFoundException;

import potatoes.server.constant.GatheringType;
import potatoes.server.constant.LocationType;
import potatoes.server.dto.CreateGatheringRequest;
import potatoes.server.dto.CreateGatheringResponse;
import potatoes.server.dto.GetDetailedGatheringResponse;
import potatoes.server.dto.GetGatheringRequest;
import potatoes.server.dto.GetGatheringResponse;
import potatoes.server.dto.SuccessResponse;
import potatoes.server.entity.Gathering;
import potatoes.server.entity.User;
import potatoes.server.entity.UserGathering;
import potatoes.server.repository.GatheringRepository;
import potatoes.server.repository.UserGatheringRepository;

@ExtendWith(MockitoExtension.class)
public class GatheringServiceTest {

	@Mock
	private GatheringRepository gatheringRepository;

	@Mock
	private UserGatheringRepository userGatheringRepository;

	@InjectMocks
	private GatheringService gatheringService;

	@Test
	void 모임생성_성공() {
		// given
		String imageUrl = "this_is_url";
		Long userId = 20L;

		CreateGatheringRequest req = CreateGatheringRequest.builder()
			.location("홍대입구")
			.type(GatheringType.DALLAEMFIT)
			.name("테스트용이름")
			.dateTime(Instant.now())
			.capacity(20)
			.registrationEnd(Instant.now().plusSeconds(3600))
			.build();

		Gathering gathering = Gathering.builder()
			.type(req.type())
			.name(req.name())
			.dateTime(req.dateTime())
			.registrationEnd(req.registrationEnd())
			.location(req.location())
			.capacity(req.capacity())
			.image(imageUrl)
			.user(User.builder().build())
			.build();

		when(gatheringRepository.save(any(Gathering.class))).thenReturn(gathering);

		// when
		CreateGatheringResponse response = gatheringService.integrateGatheringCreation(req, null, userId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("테스트용이름");
		assertThat(response.type()).isEqualTo(GatheringType.DALLAEMFIT);
		assertThat(response.location()).isEqualTo("홍대입구");
		assertThat(response.capacity()).isEqualTo(20);

		verify(gatheringRepository).save(any(Gathering.class));
	}

	@Test
	void 모임_참여_성공() {
		// Given
		Long userId = 1L;
		Long gatheringId = 1L;

		String imageUrl = "this_is_url";

		Gathering gathering = Gathering.builder()
			.type(GatheringType.DALLAEMFIT)
			.name("테스트 모임")
			.dateTime(Instant.now())
			.registrationEnd(Instant.now().plusSeconds(3600))
			.location("테스트 장소")
			.capacity(10)
			.image(imageUrl)
			.user(User.builder().build())
			.build();

		when(gatheringRepository.findByIdAndCanceledAtIsNull(gatheringId)).thenReturn(gathering);
		when(userGatheringRepository.existsByUserIdAndGatheringIdAndCanceledAtIsNull(userId, gatheringId)).thenReturn(
			false);

		// When
		SuccessResponse response = gatheringService.joinGathering(userId, gatheringId);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.message()).isEqualTo("모임에 참여했습니다");
		assertThat(gathering.getParticipantCount()).isEqualTo(2);

		verify(userGatheringRepository).save(any(UserGathering.class));
	}

	@Test
	void 모임_참여_실패_이미_참여한_경우() {
		// Given
		Long userId = 1L;
		Long gatheringId = 1L;

		Gathering gathering = Gathering.builder()
			.type(GatheringType.DALLAEMFIT)
			.name("테스트 모임")
			.dateTime(Instant.now())
			.registrationEnd(Instant.now().plusSeconds(3600))
			.location("테스트 장소")
			.capacity(10)
			.user(User.builder().build())
			.build();

		when(gatheringRepository.findByIdAndCanceledAtIsNull(gatheringId)).thenReturn(gathering);
		when(userGatheringRepository.existsByUserIdAndGatheringIdAndCanceledAtIsNull(userId, gatheringId)).thenReturn(
			true);

		// When & Then
		assertThatThrownBy(() -> gatheringService.joinGathering(userId, gatheringId))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("이미 참여한 모임입니다.");
	}

	@Test
	void 모임_참여_실패_정원초과() {
		// Given
		Long userId = 1L;
		Long gatheringId = 1L;

		Gathering gathering = Gathering.builder()
			.type(GatheringType.DALLAEMFIT)
			.name("테스트 모임")
			.dateTime(Instant.now())
			.registrationEnd(Instant.now().plusSeconds(3600))
			.location("테스트 장소")
			.capacity(1)
			.user(User.builder().build())
			.build();

		when(gatheringRepository.findByIdAndCanceledAtIsNull(gatheringId)).thenReturn(gathering);
		when(userGatheringRepository.existsByUserIdAndGatheringIdAndCanceledAtIsNull(userId, gatheringId))
			.thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> gatheringService.joinGathering(userId, gatheringId))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("모임 정원이 초과되었습니다.");

		assertThat(gathering.getParticipantCount()).isEqualTo(1);

		verify(userGatheringRepository, never()).save(any(UserGathering.class));
	}

	@Test
	void 모임_참여취소_성공() {
		// Given
		Long userId = 1L;
		Long gatheringId = 1L;
		Instant futureDateTime = Instant.now().plusSeconds(3600);

		Gathering gathering = Gathering.builder()
			.type(GatheringType.DALLAEMFIT)
			.name("테스트 모임")
			.dateTime(futureDateTime)
			.registrationEnd(futureDateTime)
			.location("테스트 장소")
			.capacity(10)
			.user(User.builder().build())
			.build();

		gathering.increaseParticipantCount();

		UserGathering userGathering = UserGathering.builder()
			.user(User.builder().build())
			.gathering(gathering)
			.joinedAt(Instant.now())
			.build();

		when(gatheringRepository.findByIdAndCanceledAtIsNull(gatheringId)).thenReturn(gathering);
		when(userGatheringRepository.findByUserIdAndGatheringIdAndCanceledAtIsNull(userId, gatheringId))
			.thenReturn(Optional.of(userGathering));

		// When
		SuccessResponse response = gatheringService.cancelGatheringParticipation(userId, gatheringId);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.message()).isEqualTo("모임을 참여 취소했습니다.");
		assertThat(gathering.getParticipantCount()).isEqualTo(1);
		assertThat(userGathering.getCanceledAt()).isNotNull();
	}

	@Test
	void 모임_참여취소_실패_이미_지난_모임() {
		// Given
		Long userId = 1L;
		Long gatheringId = 1L;
		Instant pastDateTime = Instant.now().minusSeconds(3600);  // 과거 시간

		Gathering gathering = Gathering.builder()
			.type(GatheringType.DALLAEMFIT)
			.name("테스트 모임")
			.dateTime(pastDateTime)
			.registrationEnd(pastDateTime)
			.location("테스트 장소")
			.capacity(10)
			.user(User.builder().build())
			.build();

		when(gatheringRepository.findByIdAndCanceledAtIsNull(gatheringId)).thenReturn(gathering);

		// When & Then
		assertThatThrownBy(() -> gatheringService.cancelGatheringParticipation(userId, gatheringId))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("이미 지난 모임은 참여 취소가 불가능합니다.");
	}

	@Test
	void 모임_참여취소_실패_참여하지_않은_모임() {
		// Given
		Long userId = 1L;
		Long gatheringId = 1L;
		Instant futureDateTime = Instant.now().plusSeconds(3600);

		Gathering gathering = Gathering.builder()
			.type(GatheringType.DALLAEMFIT)
			.name("테스트 모임")
			.dateTime(futureDateTime)
			.registrationEnd(futureDateTime)
			.location("테스트 장소")
			.capacity(10)
			.user(User.builder().build())
			.build();

		when(gatheringRepository.findByIdAndCanceledAtIsNull(gatheringId)).thenReturn(gathering);
		when(userGatheringRepository.findByUserIdAndGatheringIdAndCanceledAtIsNull(userId, gatheringId))
			.thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> gatheringService.cancelGatheringParticipation(userId, gatheringId))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("참여하지 않은 모임입니다.");
	}

	@Test
	void 모임_목록_조회_성공() {
		// Given
		GetGatheringRequest request = GetGatheringRequest.builder()
			.type(GatheringType.DALLAEMFIT)
			.location(LocationType.홍대입구)
			.date("2024-03-28")
			.createdBy(1L)
			.sortBy("dateTime")
			.sortOrder("asc")
			.limit(20)
			.offset(0)
			.build();

		Pageable pageable = PageRequest.of(0, 20);

		List<Gathering> gatherings = List.of(
			Gathering.builder()
				.type(GatheringType.DALLAEMFIT)
				.name("모임1")
				.dateTime(Instant.now())
				.registrationEnd(Instant.now().plusSeconds(3600))
				.location("홍대입구")
				.capacity(10)
				.user(User.builder().build())
				.build(),
			Gathering.builder()
				.type(GatheringType.DALLAEMFIT)
				.name("모임2")
				.dateTime(Instant.now())
				.registrationEnd(Instant.now().plusSeconds(3600))
				.location("홍대입구")
				.capacity(10)
				.user(User.builder().build())
				.build()
		);

		Page<Gathering> gatheringPage = new PageImpl<>(gatherings);

		when(gatheringRepository.findGatheringsWithFilters(
			request.ids(),
			request.type(),
			request.location(),
			request.date(),
			request.createdBy(),
			pageable
		)).thenReturn(gatheringPage);

		// When
		List<GetGatheringResponse> responses = gatheringService.getGatherings(request, pageable);

		// Then
		assertThat(responses).hasSize(2);
		assertThat(responses.get(0).name()).isEqualTo("모임1");
		assertThat(responses.get(1).name()).isEqualTo("모임2");
		assertThat(responses.get(0).type()).isEqualTo(GatheringType.DALLAEMFIT);
		assertThat(responses.get(0).location()).isEqualTo("홍대입구");

		verify(gatheringRepository).findGatheringsWithFilters(
			request.ids(),
			request.type(),
			request.location(),
			request.date(),
			request.createdBy(),
			pageable
		);
	}

	@Test
	void 모임_목록_조회_결과없음() {
		// Given
		GetGatheringRequest request = GetGatheringRequest.builder()
			.type(GatheringType.DALLAEMFIT)
			.location(LocationType.신림)
			.date("2024-03-28")
			.createdBy(1L)
			.sortBy("dateTime")
			.sortOrder("asc")
			.limit(20)
			.offset(0)
			.build();

		Pageable pageable = PageRequest.of(0, 20);

		// 빈 페이지 반환
		Page<Gathering> emptyPage = new PageImpl<>(List.of());

		when(gatheringRepository.findGatheringsWithFilters(
			request.ids(),
			request.type(),
			request.location(),
			request.date(),
			request.createdBy(),
			pageable
		)).thenReturn(emptyPage);

		// When
		List<GetGatheringResponse> responses = gatheringService.getGatherings(request, pageable);

		// Then
		assertThat(responses).isEmpty();

		verify(gatheringRepository).findGatheringsWithFilters(
			request.ids(),
			request.type(),
			request.location(),
			request.date(),
			request.createdBy(),
			pageable
		);
	}

	@Test
	void 특정_모임_상세조회_성공() {
		// Given
		Long gatheringId = 1L;

		Gathering gathering = Gathering.builder()
			.type(GatheringType.DALLAEMFIT)
			.name("테스트 모임")
			.dateTime(Instant.now())
			.registrationEnd(Instant.now().plusSeconds(3600))
			.location("홍대입구")
			.capacity(10)
			.user(User.builder().build())
			.build();

		when(gatheringRepository.findByIdAndCanceledAtIsNull(gatheringId)).thenReturn(gathering);

		// When
		GetDetailedGatheringResponse response = gatheringService.getDetailedGathering(gatheringId);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("테스트 모임");
		assertThat(response.type()).isEqualTo(GatheringType.DALLAEMFIT);
		assertThat(response.location()).isEqualTo("홍대입구");
		assertThat(response.capacity()).isEqualTo(10);
		assertThat(response.participantCount()).isEqualTo(1);
	}

	@Test
	void 특정_모임_상세조회_실패_존재하지않는모임() {
		// Given
		Long gatheringId = 999L;

		when(gatheringRepository.findByIdAndCanceledAtIsNull(gatheringId)).thenReturn(null);

		// When & Then
		assertThatThrownBy(() -> gatheringService.getDetailedGathering(gatheringId))
			.isInstanceOf(NotFoundException.class)
			.hasMessage("존재하지 않는 모임이거나 이미 취소된 모임입니다.");
	}
}

