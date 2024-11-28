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
import potatoes.server.entity.Gathering;
import potatoes.server.entity.User;
import potatoes.server.repository.GatheringRepository;

@ExtendWith(MockitoExtension.class)
public class GatheringServiceTest {

	@Mock
	private GatheringRepository gatheringRepository;

	@InjectMocks
	private GatheringService gatheringService;

	@Test
	void 모임생성_성공() {
		// given
		String imageUrl = "this_is_url";
		Long userId = 20L;

		User user = User.builder()
			.email("test@test.com")
			.password("password")
			.name("테스트유저")
			.companyName("테스트회사")
			.image("test-profile-image")
			.build();

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
			.user(user)
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
}
