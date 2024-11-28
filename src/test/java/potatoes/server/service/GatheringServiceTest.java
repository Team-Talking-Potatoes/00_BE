package potatoes.server.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import potatoes.server.constant.GatheringType;
import potatoes.server.dto.CreateGatheringRequest;
import potatoes.server.dto.CreateGatheringResponse;
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
}

