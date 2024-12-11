package potatoes.server.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import potatoes.server.dto.SignInRequest;
import potatoes.server.dto.SignUpRequest;
import potatoes.server.error.exception.DuplicationEmail;
import potatoes.server.error.exception.InvalidSignInInformation;
import potatoes.server.service.AuthService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void 로그인_성공_시_쿠키_헤더_설정() throws Exception {
		// given
		SignInRequest request = new SignInRequest("test@example.com", "password123");
		ResponseCookie mockCookie = ResponseCookie.from("AUTH-TOKEN", "token123")
			.httpOnly(true)
			.path("/")
			.build();

		when(authService.signIn(any(SignInRequest.class)))
			.thenReturn(mockCookie);

		// when & then
		mockMvc.perform(post("/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.SET_COOKIE))
			.andExpect(header().string(HttpHeaders.SET_COOKIE, mockCookie.toString()));
	}

	@Test
	void 회원가입_실패_이메일_중복() throws Exception {
		// given
		SignUpRequest request = new SignUpRequest(
			"existing@example.com",
			"password123",
			"testName",
			"testNickname",
			"20241010",
			"010-1234-5678"
		);

		doThrow(new DuplicationEmail())
			.when(authService).signUp(any(SignUpRequest.class));

		// when & then
		mockMvc.perform(post("/auth/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().is(400));
	}

	@Test
	void 로그인_실패_비밀번호_불일치() throws Exception {
		// given
		SignInRequest request = new SignInRequest("test@example.com", "wrongPassword");

		when(authService.signIn(any(SignInRequest.class)))
			.thenThrow(new InvalidSignInInformation());

		// when & then
		mockMvc.perform(post("/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@Test
	void 회원가입_성공() throws Exception {
		// given
		SignUpRequest request = new SignUpRequest(
			"test@example.com",
			"password123",
			"testName",
			"testNickname",
			"20241010",
			"010-1234-5678"
		);

		// when & then
		mockMvc.perform(post("/auth/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isOk());

		verify(authService).signUp(any(SignUpRequest.class));
	}
}
