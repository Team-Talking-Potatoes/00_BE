package potatoes.server.mail.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {
	@Bean
	public GroupedOpenApi getMailApi() {
		return GroupedOpenApi.builder()
			.group("mail")
			.packagesToScan("potatoes.server.mail.controller")
			.build();
	}
}
