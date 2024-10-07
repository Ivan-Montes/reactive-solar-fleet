package dev.ime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;

@Configuration
public class KafkaConfig {

	@Bean
	KafkaAdmin.NewTopics topics() {
		
		return new NewTopics(
				TopicBuilder.name(GlobalConstants.CREWMEMBER_CREATED).build(),
				TopicBuilder.name(GlobalConstants.CREWMEMBER_UPDATED).build(),
				TopicBuilder.name(GlobalConstants.CREWMEMBER_DELETED).build()
				);
		
	}
	
}
