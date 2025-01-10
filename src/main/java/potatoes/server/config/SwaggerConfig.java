package potatoes.server.config;

import java.util.ArrayList;
import java.util.List;

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
}
