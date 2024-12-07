package potatoes.server.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import potatoes.server.controller.MailController;

@Configuration
public class MailConfig {
	@Bean
	public GroupedOpenApi getMailApi() {
		return GroupedOpenApi.builder()
			.group("mail")
			.addOpenApiMethodFilter(method ->
				method.getDeclaringClass().equals(MailController.class))
			.packagesToScan("potatoes.server.controller")
			.build();
	}
}
