package potatoes.server.controller;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static potatoes.server.error.ErrorCode.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import potatoes.server.entity.User;
import potatoes.server.repository.TravelPlanRepository;
import potatoes.server.repository.TravelRepository;
import potatoes.server.repository.TravelUserRepository;
import potatoes.server.repository.UserRepository;
import potatoes.server.utils.jwt.JwtTokenUtil;
import potatoes.server.utils.s3.S3UtilsProvider;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class TravelControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private S3UtilsProvider s3UtilsProvider;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TravelRepository travelRepository;

	@Autowired
	private TravelUserRepository travelUserRepository;

	@Autowired
	private TravelPlanRepository travelPlanRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@BeforeEach
	void setUp() {
		// S3 모킹
		when(s3UtilsProvider.uploadFile(any(MultipartFile.class)))
			.thenReturn("mocked-url");

		travelPlanRepository.deleteAll();
		travelUserRepository.deleteAll();
		travelRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void 여행_생성_성공() throws Exception {

		// given
		User mockUser = User.builder()
			.email("test@gmail.com")
			.password("1234")
			.nickname("testUser")
			.birthDate(20241010)
			.contact("010-0000-0000")
			.build();
		userRepository.save(mockUser);
		String token = jwtTokenUtil.createToken(String.valueOf(mockUser.getId()));

		MockMultipartFile travelImage = new MockMultipartFile(
			"travelImage",
			"test.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test image".getBytes()
		);

		MockMultipartFile destinationImage = new MockMultipartFile(
			"detailTravel[0].destinationImage",
			"detail.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test detail image".getBytes()
		);

		// expect
		mockMvc.perform(multipart("/api/travel")
				.file(travelImage)
				.file(destinationImage)
				.param("travelName", "Test Travel")
				.param("expectedTripCost", "1000")
				.param("minTravelMateCount", "2")
				.param("maxTravelMateCount", "5")
				.param("travelDescription", "Test Description")
				.param("hashTags", "#test#tag")
				.param("isDomestic", "true")
				.param("travelLocation", "Test Location")
				.param("departureLocation", "Test Departure")
				.param("startAt", "2025-12-06")
				.param("endAt", "2025-12-07")
				.param("detailTravel[0].tripDay", "1")
				.param("detailTravel[0].tripOrderNumber", "1")
				.param("detailTravel[0].destination", "Test Destination")
				.param("detailTravel[0].description", "Test Detail Description")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isOk());
	}

	@Test
	void 여행_생성_시작_날짜가_종료_날짜_이후_일_경우_에러메시지를_반환() throws Exception {

		// given
		User mockUser = User.builder()
			.email("test@gmail.com")
			.password("1234")
			.nickname("testUser")
			.birthDate(20241010)
			.contact("010-0000-0000")
			.build();
		userRepository.save(mockUser);
		String token = jwtTokenUtil.createToken(String.valueOf(mockUser.getId()));

		MockMultipartFile travelImage = new MockMultipartFile(
			"travelImage",
			"test.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test image".getBytes()
		);

		MockMultipartFile destinationImage = new MockMultipartFile(
			"detailTravel[0].destinationImage",
			"detail.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test detail image".getBytes()
		);

		// when
		mockMvc.perform(multipart("/api/travel")
				.file(travelImage)
				.file(destinationImage)
				.param("travelName", "Test Travel")
				.param("expectedTripCost", "1000")
				.param("minTravelMateCount", "2")
				.param("maxTravelMateCount", "5")
				.param("travelDescription", "Test Description")
				.param("hashTags", "#test#tag")
				.param("isDomestic", "true")
				.param("travelLocation", "Test Location")
				.param("departureLocation", "Test Departure")
				.param("startAt", "2025-12-09")
				.param("endAt", "2025-12-07")
				.param("detailTravel[0].tripDay", "1")
				.param("detailTravel[0].tripOrderNumber", "1")
				.param("detailTravel[0].destination", "Test Destination")
				.param("detailTravel[0].description", "Test Detail Description")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_TRAVEL_DATE.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_TRAVEL_DATE.getMessage()));
	}

	@Test
	void 여행_생성_상세_일정_누락시_에러메시지_반환() throws Exception {

		// given
		User mockUser = User.builder()
			.email("test@gmail.com")
			.password("1234")
			.nickname("testUser")
			.birthDate(20241010)
			.contact("010-0000-0000")
			.build();
		userRepository.save(mockUser);
		String token = jwtTokenUtil.createToken(String.valueOf(mockUser.getId()));

		MockMultipartFile travelImage = new MockMultipartFile(
			"travelImage",
			"test.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test image".getBytes()
		);

		// expect
		mockMvc.perform(multipart("/api/travel")
				.file(travelImage)
				.param("travelName", "Test Travel")
				.param("expectedTripCost", "1000")
				.param("minTravelMateCount", "2")
				.param("maxTravelMateCount", "5")
				.param("travelDescription", "Test Description")
				.param("hashTags", "#test#tag")
				.param("isDomestic", "true")
				.param("travelLocation", "Test Location")
				.param("departureLocation", "Test Departure")
				.param("startAt", "2025-12-06")
				.param("endAt", "2025-12-07")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_INPUT_VALUE.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
			.andExpect(jsonPath("$.parameter", Matchers.hasItem(
				Matchers.allOf(
					Matchers.hasEntry("field", "detailTravel"),
					Matchers.hasEntry("reason", "여행 상세일정을 입력해주세요.")
				)
			)));
	}

	@Test
	void 여행_생성_해시태그가_5개를_초과할_경우_예외메시지_반환() throws Exception {

		// given
		User mockUser = User.builder()
			.email("test@gmail.com")
			.password("1234")
			.nickname("testUser")
			.birthDate(20241010)
			.contact("010-0000-0000")
			.build();
		userRepository.save(mockUser);
		String token = jwtTokenUtil.createToken(String.valueOf(mockUser.getId()));

		MockMultipartFile travelImage = new MockMultipartFile(
			"travelImage",
			"test.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test image".getBytes()
		);

		MockMultipartFile destinationImage = new MockMultipartFile(
			"detailTravel[0].destinationImage",
			"detail.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test detail image".getBytes()
		);

		// when
		mockMvc.perform(multipart("/api/travel")
				.file(travelImage)
				.file(destinationImage)
				.param("travelName", "Test Travel")
				.param("expectedTripCost", "1000")
				.param("minTravelMateCount", "2")
				.param("maxTravelMateCount", "5")
				.param("travelDescription", "Test Description")
				.param("hashTags", "#test#tag#1#2#3#4")
				.param("isDomestic", "true")
				.param("travelLocation", "Test Location")
				.param("departureLocation", "Test Departure")
				.param("startAt", "2025-12-06")
				.param("endAt", "2025-12-07")
				.header("Authorization", "Bearer " + token)
				.param("detailTravel[0].tripDay", "1")
				.param("detailTravel[0].tripOrderNumber", "1")
				.param("detailTravel[0].destination", "Test Destination")
				.param("detailTravel[0].description", "Test Detail Description")
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_TRAVEL_HASHTAGS_VALUE.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_TRAVEL_HASHTAGS_VALUE.getMessage()));
	}

	@Test
	void 여행_생성_모집인원_최소가_최대보다_많을_경우_예외메시지_반환() throws Exception {

		// given
		User mockUser = User.builder()
			.email("test@gmail.com")
			.password("1234")
			.nickname("testUser")
			.birthDate(20241010)
			.contact("010-0000-0000")
			.build();
		userRepository.save(mockUser);
		String token = jwtTokenUtil.createToken(String.valueOf(mockUser.getId()));

		MockMultipartFile travelImage = new MockMultipartFile(
			"travelImage",
			"test.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test image".getBytes()
		);

		MockMultipartFile destinationImage = new MockMultipartFile(
			"detailTravel[0].destinationImage",
			"detail.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test detail image".getBytes()
		);

		// when
		mockMvc.perform(multipart("/api/travel")
				.file(travelImage)
				.file(destinationImage)
				.param("travelName", "Test Travel")
				.param("expectedTripCost", "1000")
				.param("minTravelMateCount", "5")
				.param("maxTravelMateCount", "2")
				.param("travelDescription", "Test Description")
				.param("hashTags", "#test#tag")
				.param("isDomestic", "true")
				.param("travelLocation", "Test Location")
				.param("departureLocation", "Test Departure")
				.param("startAt", "2025-12-06")
				.param("endAt", "2025-12-07")
				.param("detailTravel[0].tripDay", "1")
				.param("detailTravel[0].tripOrderNumber", "1")
				.param("detailTravel[0].destination", "Test Destination")
				.param("detailTravel[0].description", "Test Detail Description")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_TRAVEL_MATE_COUNT.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_TRAVEL_MATE_COUNT.getMessage()));
	}

	@Test
	void 여행_생성_이미_지나간_날짜를_시작일_혹은_종료일로_할_경우_예외메시지_반환() throws Exception {

		// given
		User mockUser = User.builder()
			.email("test@gmail.com")
			.password("1234")
			.nickname("testUser")
			.birthDate(20241010)
			.contact("010-0000-0000")
			.build();
		userRepository.save(mockUser);
		String token = jwtTokenUtil.createToken(String.valueOf(mockUser.getId()));

		MockMultipartFile travelImage = new MockMultipartFile(
			"travelImage",
			"test.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test image".getBytes()
		);

		MockMultipartFile destinationImage = new MockMultipartFile(
			"detailTravel[0].destinationImage",
			"detail.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test detail image".getBytes()
		);

		// when
		mockMvc.perform(multipart("/api/travel")
				.file(travelImage)
				.file(destinationImage)
				.param("travelName", "Test Travel")
				.param("expectedTripCost", "1000")
				.param("minTravelMateCount", "2")
				.param("maxTravelMateCount", "5")
				.param("travelDescription", "Test Description")
				.param("hashTags", "#test#tag")
				.param("isDomestic", "true")
				.param("travelLocation", "Test Location")
				.param("departureLocation", "Test Departure")
				.param("startAt", "2020-12-06")
				.param("endAt", "2020-12-07")
				.param("detailTravel[0].tripDay", "1")
				.param("detailTravel[0].tripOrderNumber", "1")
				.param("detailTravel[0].destination", "Test Destination")
				.param("detailTravel[0].description", "Test Detail Description")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_INPUT_VALUE.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
			.andExpect(jsonPath("$.parameter", hasItems(
				allOf(
					hasEntry("field", "startAt"),
					hasEntry("value", "2020-12-06"),
					hasEntry("reason", "선택할 수 없는 날짜입니다.")
				),
				allOf(
					hasEntry("field", "endAt"),
					hasEntry("value", "2020-12-07"),
					hasEntry("reason", "선택할 수 없는 날짜입니다.")
				)
			)));
	}

	@Test
	void 여행_생성_상세_계획의_날이_잘못설정_되었을_경우_예외메시지_반환() throws Exception {

		// given
		User mockUser = User.builder()
			.email("test@gmail.com")
			.password("1234")
			.nickname("testUser")
			.birthDate(20241010)
			.contact("010-0000-0000")
			.build();
		userRepository.save(mockUser);
		String token = jwtTokenUtil.createToken(String.valueOf(mockUser.getId()));

		MockMultipartFile travelImage = new MockMultipartFile(
			"travelImage",
			"test.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test image".getBytes()
		);

		MockMultipartFile destinationImage = new MockMultipartFile(
			"detailTravel[0].destinationImage",
			"detail.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test detail image".getBytes()
		);

		// when
		mockMvc.perform(multipart("/api/travel")
				.file(travelImage)
				.file(destinationImage)
				.param("travelName", "Test Travel")
				.param("expectedTripCost", "1000")
				.param("minTravelMateCount", "2")
				.param("maxTravelMateCount", "5")
				.param("travelDescription", "Test Description")
				.param("hashTags", "#test#tag")
				.param("isDomestic", "true")
				.param("travelLocation", "Test Location")
				.param("departureLocation", "Test Departure")
				.param("startAt", "2025-12-06")
				.param("endAt", "2025-12-07")
				.param("detailTravel[0].tripDay", "5")
				.param("detailTravel[0].tripOrderNumber", "1")
				.param("detailTravel[0].destination", "Test Destination")
				.param("detailTravel[0].description", "Test Detail Description")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_TRAVEL_DETAIL_INFO.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_TRAVEL_DETAIL_INFO.getMessage()));
	}
}