package potatoes.server.chat.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import potatoes.server.chat.controller.ChatController;

@Configuration
public class ChatConfig {

	@Bean
	public GroupedOpenApi getChatApi() {
		return GroupedOpenApi.builder()
			.group("chat")
			.addOpenApiMethodFilter(method ->
				method.getDeclaringClass().equals(ChatController.class))
			.packagesToScan("potatoes.server.chat.controller")
			.build();
	}
}
