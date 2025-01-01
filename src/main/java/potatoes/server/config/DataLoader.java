package potatoes.server.config;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import potatoes.server.constant.ParticipantRole;
import potatoes.server.entity.Chat;
import potatoes.server.entity.Travel;
import potatoes.server.entity.TravelUser;
import potatoes.server.entity.User;
import potatoes.server.repository.ChatRepository;
import potatoes.server.repository.TravelRepository;
import potatoes.server.repository.TravelUserRepository;
import potatoes.server.repository.UserRepository;
import potatoes.server.utils.crypto.PasswordEncoder;

@RequiredArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {

	private final UserRepository userRepository;
	private final TravelRepository travelRepository;
	private final TravelUserRepository travelUserRepository;
	private final ChatRepository chatRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		// Mock 유저 생성
		User user = User.builder()
			.email("testUser@test.com")
			.password(passwordEncoder.encrypt("testPassword12!"))
			.name("Test User")
			.nickname("Test User")
			.birthDate(20001231)
			.contact("010-0000-0000")
			.build();
		userRepository.save(user);

		User user2 = User.builder()
			.email("testUser2@test.com")
			.password(passwordEncoder.encrypt("testPassword12!"))
			.name("Test User2")
			.nickname("Test User")
			.birthDate(20001231)
			.contact("010-0000-0000")
			.build();
		userRepository.save(user2);

		// Mock 여행 생성
		List<MockTravelData> mockTravelDataList = createMockTravelDataList();
		for (MockTravelData mockTravelData : mockTravelDataList) {
			addPreviousTravels(user, mockTravelData);
		}
	}

	private void addPreviousTravels(User user, MockTravelData mockTravelData) {
		LocalDateTime startAt = mockTravelData.getStartAt();
		LocalDateTime endAt = mockTravelData.getEndAt();
		LocalDateTime registrationEnd = mockTravelData.getRegistrationEnd();
		Duration duration = Duration.between(startAt, endAt);
		long tripDuration = duration.toDays() + 1;

		Travel travel = Travel.builder()
			.name(mockTravelData.name)
			.description(mockTravelData.description)
			.image(mockTravelData.image)
			.expectedTripCost(mockTravelData.expectedTripCost)
			.minTravelMateCount(mockTravelData.minTravelMateCount)
			.maxTravelMateCount(mockTravelData.maxTravelMateCount)
			.hashTags(mockTravelData.hashTags)
			.isDomestic(mockTravelData.isDomestic)
			.travelLocation(mockTravelData.travelLocation)
			.departureLocation(mockTravelData.departureLocation)
			.startAt(startAt.toInstant(ZoneOffset.UTC))
			.endAt(endAt.toInstant(ZoneOffset.UTC))
			.registrationEnd(registrationEnd.toInstant(ZoneOffset.UTC))
			.tripDuration((int)tripDuration)
			.build();
		travelRepository.save(travel);

		TravelUser travelUser = TravelUser.builder()
			.role(ParticipantRole.ORGANIZER)
			.travel(travel)
			.user(user)
			.build();
		travelUserRepository.save(travelUser);

		Chat chat = Chat.builder()
			.name(travel.getName())
			.host(user)
			.travel(travel)
			.currentMemberCount(1)
			.maxMemberCount(travel.getMaxTravelMateCount())
			.build();
		chatRepository.save(chat);
	}

	@Getter
	private static class MockTravelData {
		private String name;
		private String description;
		private final String image = "https://wegobucket.s3.ap-northeast-2.amazonaws.com/dev/20250101/2b2225fa-0691-48d6-a403-d5d91d57795d.jpg";
		private int expectedTripCost;
		private int minTravelMateCount;
		private int maxTravelMateCount;
		private String hashTags;
		private boolean isDomestic;
		private String travelLocation;
		private String departureLocation;
		private LocalDateTime startAt;
		private LocalDateTime endAt;
		private LocalDateTime registrationEnd;

		@Builder
		public MockTravelData(String name, String description, int expectedTripCost, int minTravelMateCount,
			int maxTravelMateCount, String hashTags, boolean isDomestic, String travelLocation,
			String departureLocation,
			LocalDateTime startAt, LocalDateTime endAt, LocalDateTime registrationEnd) {
			this.name = name;
			this.description = description;
			this.expectedTripCost = expectedTripCost;
			this.minTravelMateCount = minTravelMateCount;
			this.maxTravelMateCount = maxTravelMateCount;
			this.hashTags = hashTags;
			this.isDomestic = isDomestic;
			this.travelLocation = travelLocation;
			this.departureLocation = departureLocation;
			this.startAt = startAt;
			this.endAt = endAt;
			this.registrationEnd = registrationEnd;
		}
	}

	private List<MockTravelData> createMockTravelDataList() {
		return Arrays.asList(
			MockTravelData.builder()
				.name("제주도 한달살기")
				.description("제주도의 푸른 바다를 느껴보세요")
				.expectedTripCost(2000000)
				.minTravelMateCount(2)
				.maxTravelMateCount(4)
				.hashTags("#제주#힐링")
				.isDomestic(true)
				.travelLocation("제주도")
				.departureLocation("김포 국제 공항")
				.startAt(LocalDateTime.of(2024, 1, 1, 0, 0))
				.endAt(LocalDateTime.of(2024, 1, 30, 0, 0))
				.registrationEnd(LocalDateTime.of(2023, 12, 25, 0, 0))
				.build(),
			MockTravelData.builder()
				.name("도쿄 여행")
				.description("일본 도쿄의 현대적인 문화")
				.expectedTripCost(1500000)
				.minTravelMateCount(2)
				.maxTravelMateCount(6)
				.hashTags("#도쿄#일본")
				.isDomestic(false)
				.travelLocation("도쿄")
				.departureLocation("김포 국제 공항")
				.startAt(LocalDateTime.of(2024, 2, 1, 0, 0))
				.endAt(LocalDateTime.of(2024, 2, 7, 0, 0))
				.registrationEnd(LocalDateTime.of(2024, 1, 25, 0, 0))
				.build()
		);
	}
}
