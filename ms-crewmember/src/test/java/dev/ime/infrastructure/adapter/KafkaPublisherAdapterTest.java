package dev.ime.infrastructure.adapter;


import static org.mockito.Mockito.times;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import dev.ime.application.exception.PublishEventException;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.event.Event;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class KafkaPublisherAdapterTest {

	@Mock
	private KafkaTemplate<String, Object> kafkaTemplate;
	
	@Mock
	private LoggerUtil loggerUtil;	

	@InjectMocks
	private KafkaPublisherAdapter kafkaPublisherAdapter;

	private Event event;
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREWMEMBER_CAT;
	private final String eventType = GlobalConstants.CREWMEMBER_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();
    private final ProducerRecord<String, Object> producerRecord = new ProducerRecord<>("topic", "key", "value");

	@BeforeEach
	private void setUp() {
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
	}	

	@SuppressWarnings("unchecked")
	@Test
	void publishEvent_WithEventOk_CompleteFlow() {
		
		CompletableFuture<SendResult<String, Object>> completableFuture = new CompletableFuture<>();
		SendResult<String, Object> sendResult = Mockito.mock(SendResult.class);
		completableFuture.complete(sendResult);		
		Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class))).thenReturn(completableFuture);
	    Mockito.when(sendResult.getProducerRecord()).thenReturn(producerRecord);

		StepVerifier
		.create(kafkaPublisherAdapter.publishEvent(event))
        .verifyComplete();
		
		Mockito.verify(kafkaTemplate).send(Mockito.any(ProducerRecord.class));
		Mockito.verify(sendResult, times(2)).getProducerRecord();
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void publishEvent_WithError_HandlesErrorCorrectly() {
	    
	    CompletableFuture<SendResult<String, Object>> completableFuture = new CompletableFuture<>();
	    completableFuture.completeExceptionally(new KafkaException(GlobalConstants.MSG_PUBLISH_FAIL));	    
	    Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class))).thenReturn(completableFuture);

	    StepVerifier
	        .create(kafkaPublisherAdapter.publishEvent(event))
	        .expectError(PublishEventException.class)
	        .verify();

	    Mockito.verify(kafkaTemplate).send(Mockito.any(ProducerRecord.class));
	    
	}

}
