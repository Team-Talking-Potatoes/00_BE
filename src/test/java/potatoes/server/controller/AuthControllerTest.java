package potatoes.server.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static potatoes.server.error.ErrorCode.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import potatoes.server.dto.CreateUserRequest;
import potatoes.server.dto.SignInUserRequest;
import potatoes.server.dto.UpdateUserRequest;
import potatoes.server.entity.User;
import potatoes.server.error.exception.UserNotFound;
import potatoes.server.repository.UserRepository;
import potatoes.server.utils.crypto.PasswordEncoder;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void clean() {
		userRepository.deleteAll();
	}

	@Test
	void 회원가입_성공() throws Exception {
		// given
		CreateUserRequest request = new CreateUserRequest("hello@gmail.com", "1234", "newUserName",
			"company");

		// when
		mockMvc.perform(post("/auths/signup")
				.content(objectMapper.writeValueAsString(request))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("사용자 생성 성공"));

		// then
		assertThat(userRepository.count()).isEqualTo(1L);

		User user = userRepository.findAll().get(0);
		assertThat(user.getEmail()).isEqualTo(request.email());
		assertThat(passwordEncoder.matches(request.password(), user.getPassword())).isTrue();
		assertThat(user.getName()).isEqualTo(request.name());
		assertThat(user.getCompanyName()).isEqualTo(request.companyName());
	}

	@Test
	void 회원가입_유효하지_않은_이메일_주소를_입력할_경우_에러메시지를_반환한다() throws Exception {
		// given
		CreateUserRequest createUserRequest = new CreateUserRequest("hellogmail.com", "1234", "newUserName",
			"company");

		// expected
		mockMvc.perform(post("/auths/signup")
				.content(objectMapper.writeValueAsString(createUserRequest))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_INPUT_VALUE.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
			.andExpect(jsonPath("$.parameter", hasItems(
				allOf(
					hasEntry("field", "email"),
					hasEntry("value", "hellogmail.com"),
					hasEntry("reason", "이메일 형식으로 입력해주세요.")
				)
			)));
	}

	@Test
	void 회원가입_이메일_중복될_경우_에러메시지를_반환한다() throws Exception {
		// given
		User existingUser = User.builder()
			.email("test@example.com")
			.name("user1")
			.password(passwordEncoder.encrypt("1234"))
			.companyName("Test Company")
			.build();
		userRepository.save(existingUser);

		CreateUserRequest newUser = new CreateUserRequest(existingUser.getEmail(), "4321", "newUser",
			"company2");

		// expected
		mockMvc.perform(post("/auths/signup")
				.content(objectMapper.writeValueAsString(newUser))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("EMAIL_DUPLICATION"))
			.andExpect(jsonPath("$.message").value("이미 가입된 이메일입니다."));
	}

	@Test
	void 회원가입_이메일이_누락되었을_경우_에러메시지를_반환한다() throws Exception {
		// given
		CreateUserRequest createUserRequest = new CreateUserRequest("", "1234", "newUserName",
			"company");

		// expected
		mockMvc.perform(post("/auths/signup")
				.content(objectMapper.writeValueAsString(createUserRequest))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_INPUT_VALUE.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
			.andExpect(jsonPath("$.parameter", hasItems(
				allOf(
					hasEntry("field", "email"),
					hasEntry("value", ""),
					hasEntry("reason", "이메일는 필수입니다.")
				)
			)));
		assertThat(userRepository.count()).isEqualTo(0L);
	}

	@Test
	void 회원가입_비밀번호가_누락되었을_경우_에러메시지를_반환한다() throws Exception {
		// given
		CreateUserRequest createUserRequest = new CreateUserRequest("hello@gmail.com", "", "newUserName",
			"company");

		// expected
		mockMvc.perform(post("/auths/signup")
				.content(objectMapper.writeValueAsString(createUserRequest))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_INPUT_VALUE.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
			.andExpect(jsonPath("$.parameter", hasItems(
				allOf(
					hasEntry("field", "password"),
					hasEntry("value", ""),
					hasEntry("reason", "비밀번호는 필수입니다.")
				)
			)));
		assertThat(userRepository.count()).isEqualTo(0L);
	}

	@Test
	void 회원가입_이름이_누락되었을_경우_에러메시지를_반환한다() throws Exception {
		// given
		CreateUserRequest createUserRequest = new CreateUserRequest("hello@gmail.com", "1234", "",
			"company");

		// expected
		mockMvc.perform(post("/auths/signup")
				.content(objectMapper.writeValueAsString(createUserRequest))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_INPUT_VALUE.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
			.andExpect(jsonPath("$.parameter", hasItems(
				allOf(
					hasEntry("field", "name"),
					hasEntry("value", ""),
					hasEntry("reason", "이름은 필수입니다.")
				)
			)));
		assertThat(userRepository.count()).isEqualTo(0L);
	}

	@Test
	void 회원가입_회사이름이_누락되었을_경우_에러메시지를_반환한다() throws Exception {
		// given
		CreateUserRequest createUserRequest = new CreateUserRequest("hello@gmail.com", "1234", "user",
			"");

		// expected
		mockMvc.perform(post("/auths/signup")
				.content(objectMapper.writeValueAsString(createUserRequest))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_INPUT_VALUE.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
			.andExpect(jsonPath("$.parameter", hasItems(
				allOf(
					hasEntry("field", "companyName"),
					hasEntry("value", ""),
					hasEntry("reason", "회사이름은 필수입니다.")
				)
			)));

		assertThat(userRepository.count()).isEqualTo(0L);
	}

	@Test
	void 회원가입_필수값이_여러개_누락되었을_경우_에러메시지를_반환한다() throws Exception {
		// given
		CreateUserRequest createUserRequest = new CreateUserRequest("hello@gmail.com", "1234", "",
			"");

		// expected
		mockMvc.perform(post("/auths/signup")
				.content(objectMapper.writeValueAsString(createUserRequest))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_INPUT_VALUE.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
			.andExpect(jsonPath("$.parameter", hasItems(
				allOf(
					hasEntry("field", "companyName"),
					hasEntry("value", ""),
					hasEntry("reason", "회사이름은 필수입니다.")
				),
				allOf(
					hasEntry("field", "name"),
					hasEntry("value", ""),
					hasEntry("reason", "이름은 필수입니다.")
				)
			)));
		assertThat(userRepository.count()).isEqualTo(0L);
	}
	@Test
	void 로그인_성공() throws Exception {
		// given
		User existingUser = User.builder()
			.email("test@example.com")
			.name("user1")
			.password(passwordEncoder.encrypt("1234"))
			.companyName("Test Company")
			.build();
		userRepository.save(existingUser);
		SignInUserRequest request = new SignInUserRequest(existingUser.getEmail(), "1234");

		// expected
		mockMvc.perform(post("/auths/signin")
				.content(objectMapper.writeValueAsString(request))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	void 로그인_비밀번호가_틀릴_경우_에러메시지를_반환한다() throws Exception {
		// given
		User existingUser = User.builder()
			.email("test@example.com")
			.name("user1")
			.password(passwordEncoder.encrypt("1234"))
			.companyName("Test Company")
			.build();
		userRepository.save(existingUser);
		SignInUserRequest request = new SignInUserRequest(existingUser.getEmail(), "wrongPassword");

		// expected
		mockMvc.perform(post("/auths/signin")
				.content(objectMapper.writeValueAsString(request))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_CREDENTIALS.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_CREDENTIALS.getMessage()));
	}

	@Test
	void 로그인_이메일이_존재하지_않은_경우_에러메시지를_반환한다() throws Exception {
		// given
		User existingUser = User.builder()
			.email("test@example.com")
			.name("user1")
			.password(passwordEncoder.encrypt("1234"))
			.companyName("Test Company")
			.build();
		userRepository.save(existingUser);
		SignInUserRequest request = new SignInUserRequest("test@gmail.com", "1234");

		// expected
		mockMvc.perform(post("/auths/signin")
				.content(objectMapper.writeValueAsString(request))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_CREDENTIALS.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_CREDENTIALS.getMessage()));
	}

	@Test
	void 로그인_이메일_혹은_비밀번호를_누락되었을_경우_에러메시지를_반환한다() throws Exception {
		//given
		SignInUserRequest request = new SignInUserRequest("", "");

		// expected
		mockMvc.perform(post("/auths/signin")
				.content(objectMapper.writeValueAsString(request))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_INPUT_VALUE.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
			.andExpect(jsonPath("$.parameter", hasItems(
				allOf(
					hasEntry("field", "email"),
					hasEntry("value", request.email()),
					hasEntry("reason", "이메일는 필수입니다.")
				),
				allOf(
					hasEntry("field", "password"),
					hasEntry("value", request.password()),
					hasEntry("reason", "비밀번호는 필수입니다.")
				)
			)));
	}

	@Test
	void 로그인_이메일형식이_아닌_값일_경우_에러메시지를_반환한다() throws Exception {
		//given
		SignInUserRequest request = new SignInUserRequest("testgmailcom", "1234");

		// expected
		mockMvc.perform(post("/auths/signin")
				.content(objectMapper.writeValueAsString(request))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INVALID_INPUT_VALUE.getCode()))
			.andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
			.andExpect(jsonPath("$.parameter", hasItems(
				allOf(
					hasEntry("field", "email"),
					hasEntry("value", request.email()),
					hasEntry("reason", "이메일 형식으로 입력해주세요.")
				)
			)));
	}

}