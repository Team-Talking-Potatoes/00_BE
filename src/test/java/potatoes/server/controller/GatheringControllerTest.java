package potatoes.server.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import potatoes.server.constant.GatheringType;
import potatoes.server.constant.LocationType;
import potatoes.server.entity.Gathering;
import potatoes.server.entity.User;
import potatoes.server.repository.GatheringRepository;
import potatoes.server.repository.UserGatheringRepository;
import potatoes.server.repository.UserRepository;
import potatoes.server.utils.jwt.JwtTokenUtil;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class GatheringControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private GatheringRepository gatheringRepository;

	@Autowired
	private UserGatheringRepository userGatheringRepository;
	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void clean() {
		userGatheringRepository.deleteAll();
		gatheringRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void 모임_목록_단건_조회_성공() throws Exception {
		// given
		User user1 = createAndSaveUser("테스트1", "123", "회사1");
		createAndSaveGathering("모임1", user1, 10);

		//when then
		mockMvc.perform(get("/gatherings/")
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].name").value("모임1"))
			.andExpect(jsonPath("$[0].capacity").value(10))
			.andExpect(jsonPath("$[0].type").value("MINDFULNESS"));
	}

	@Test
	void 모임_목록_복수건_조회_조건추가_성공() throws Exception {
		//given
		User user1 = createAndSaveUser("테스트1", "123", "회사1");
		User user2 = createAndSaveUser("테스트2", "234", "회사2");
		createAndSaveGathering("모임1", user1, 10);
		createAndSaveGathering("모임2", user2, 20);

		//when then
		mockMvc.perform(get("/gatherings/")
				.param("type", GatheringType.MINDFULNESS.name())
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].name").value("모임1"))
			.andExpect(jsonPath("$[0].capacity").value(10))
			.andExpect(jsonPath("$[0].type").value("MINDFULNESS"))
			.andExpect(jsonPath("$[1].name").value("모임2"))
			.andExpect(jsonPath("$[1].capacity").value(20))
			.andExpect(jsonPath("$[1].type").value("MINDFULNESS"));
	}

	@Test
	void 모임_상세_조회_성공() throws Exception {
		// given
		User user1 = createAndSaveUser("테스트1", "123", "회사1");
		Gathering savedGathering = createAndSaveGathering("모임1", user1, 10);
		String token = jwtTokenUtil.createToken(String.valueOf(user1.getId()));
		// when then
		mockMvc.perform(get("/gatherings/{gatheringId}", savedGathering.getId())
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(savedGathering.getId()))
			.andExpect(jsonPath("$.name").value("모임1"))
			.andExpect(jsonPath("$.type").value("MINDFULNESS"))
			.andExpect(jsonPath("$.location").value("신림"))
			.andExpect(jsonPath("$.capacity").value(10))
			.andExpect(jsonPath("$.dateTime").value("2024-04-13T10:00:00Z"))
			.andExpect(jsonPath("$.registrationEnd").value("2024-04-12T10:00:00Z"));
	}

	@Test
	void 모임_취소_성공() throws Exception {
		// given
		User user1 = createAndSaveUser("테스트1", "123", "회사1");
		Gathering gathering = Gathering.builder()
			.name("모임1")
			.type(GatheringType.MINDFULNESS)
			.location("location1")
			.dateTime(Instant.parse("2024-04-13T10:00:00Z"))
			.capacity(5)
			.registrationEnd(Instant.parse("2024-04-12T10:00:00Z"))
			.user(user1)
			.build();

		gathering.increaseParticipantCount();
		Gathering savedGathering = gatheringRepository.save(gathering);


		String token = jwtTokenUtil.createToken(String.valueOf(user1.getId()));

		// when then
		mockMvc.perform(put("/gatherings/{gatheringId}/cancel", savedGathering.getId())
				.header("Authorization", "Bearer " + token))
			.andDo(print())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.type").value("MINDFULNESS"))
			.andExpect(jsonPath("$.name").value("모임1"))
			.andExpect(jsonPath("$.dateTime").value("2024-04-13T10:00:00Z"))
			.andExpect(jsonPath("$.registrationEnd").value("2024-04-12T10:00:00Z"))
			.andExpect(jsonPath("$.location").value("location1"))
			.andExpect(jsonPath("$.participantCount").value(1))
			.andExpect(jsonPath("$.capacity").value(5))
			.andExpect(jsonPath("$.createdBy").value(savedGathering.getCreatedBy()))
			.andExpect(jsonPath("$.canceledAt").isNotEmpty());
	}

	@Test
	void 모임_참여_성공() throws Exception {
		// given
		User host = createAndSaveUser("호스트", "host@test.com", "회사1");
		User participant = createAndSaveUser("참여자", "participant@test.com", "회사2");

		Gathering gathering = Gathering.builder()
			.name("모임1")
			.type(GatheringType.MINDFULNESS)
			.location("location1")
			.dateTime(Instant.parse("2024-04-13T10:00:00Z"))
			.capacity(5)
			.registrationEnd(Instant.parse("2024-04-12T10:00:00Z"))
			.user(host)
			.build();
		Gathering savedGathering = gatheringRepository.save(gathering);

		String token = jwtTokenUtil.createToken(String.valueOf(participant.getId()));

		// when then
		mockMvc.perform(post("/gatherings/{gatheringId}/join", savedGathering.getId())
				.header("Authorization", "Bearer " + token))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.message").exists());
	}

	private User createAndSaveUser(String name, String email, String companyName) {
		User user = User.builder()
			.name(name)
			.email(email)
			.companyName(companyName)
			.password("12345")
			.build();
		return userRepository.save(user);
	}

	private Gathering createAndSaveGathering(String name, User user, int capacity) {
		Gathering gathering = Gathering.builder()
			.name(name)
			.type(GatheringType.MINDFULNESS)
			.location(LocationType.신림.name())
			.dateTime(Instant.parse("2024-04-13T10:00:00Z"))
			.capacity(capacity)
			.registrationEnd(Instant.parse("2024-04-12T10:00:00Z"))
			.user(user)
			.build();
		return gatheringRepository.save(gathering);
	}

	//FIXME 현재 파라미터에 한국어 들어가면 터지는 문제 존재
}
