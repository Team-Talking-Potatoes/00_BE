package potatoes.server.config;

import java.util.ArrayList;
import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import potatoes.server.auth.controller.AuthController;
import potatoes.server.chat.controller.ChatController;
import potatoes.server.controller.MailController;
import potatoes.server.controller.TravelController;
import potatoes.server.review.controller.ReviewController;
import potatoes.server.user.controller.UserController;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

	public SwaggerConfig(MappingJackson2HttpMessageConverter converter) {
		List<MediaType> supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
		supportedMediaTypes.add(new MediaType("application", "octet-stream"));
		converter.setSupportedMediaTypes(supportedMediaTypes);
	}

	@Bean
	public OpenAPI customOpenAPI() {
		SecurityScheme cookieScheme = new SecurityScheme()
			.type(SecurityScheme.Type.APIKEY)
			.in(SecurityScheme.In.HEADER)
			.name("Set-Cookie");

		Components components = new Components()
			.addSecuritySchemes("cookieAuth", cookieScheme);

		SecurityRequirement requirement = new SecurityRequirement()
			.addList("cookieAuth");

		Info info = new Info()
			.title("talking-potatoes-api")
			.version("1.0");

		return new OpenAPI()
			.components(components)
			.info(info)
			.addSecurityItem(requirement);
		//FIXME 쿠키방식으로 변경되면서 쿠키를 스웨거에서 인식하게 해야하는데 인식을 못함
	}

	@Bean
	public GroupedOpenApi getTravelApi() {
		return GroupedOpenApi.builder()
			.group("travel")
			.addOpenApiMethodFilter(method ->
				method.getDeclaringClass().equals(TravelController.class))
			.packagesToScan("potatoes.server.controller")
			.build();
	}

	@Bean
	public GroupedOpenApi getMailApi() {
		return GroupedOpenApi.builder()
			.group("mail")
			.addOpenApiMethodFilter(method ->
				method.getDeclaringClass().equals(MailController.class))
			.packagesToScan("potatoes.server.controller")
			.build();
	}

	@Bean
	public GroupedOpenApi getAuthApi() {
		return GroupedOpenApi.builder()
			.group("auth")
			.addOpenApiMethodFilter(method ->
				method.getDeclaringClass().equals(AuthController.class))
			.packagesToScan("potatoes.server.controller")
			.build();
	}

	@Bean
	public GroupedOpenApi getUserApi() {
		return GroupedOpenApi.builder()
			.group("user")
			.addOpenApiMethodFilter(method ->
				method.getDeclaringClass().equals(UserController.class))
			.packagesToScan("potatoes.server.controller")
			.build();
	}

	@Bean
	public GroupedOpenApi getReviewApi() {
		return GroupedOpenApi.builder()
			.group("review")
			.addOpenApiMethodFilter(method ->
				method.getDeclaringClass().equals(ReviewController.class))
			.packagesToScan("potatoes.server.controller")
			.build();
	}

	@Bean
	public GroupedOpenApi getChatApi() {
		return GroupedOpenApi.builder()
			.group("chat")
			.addOpenApiMethodFilter(method ->
				method.getDeclaringClass().equals(ChatController.class))
			.packagesToScan("potatoes.server.controller")
			.build();
	}
}
