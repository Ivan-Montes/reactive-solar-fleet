package dev.ime.config;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaAdmin;

@ExtendWith(MockitoExtension.class)
class KafkaConfigTest {

	@InjectMocks
	private KafkaConfig kafkaConfig;
	
	@Test
	void topics_ByDefault_ReturnNewTopics() {
		
		KafkaAdmin.NewTopics topics = kafkaConfig.topics();
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(topics).isNotNull()
				);
		
	}

}
