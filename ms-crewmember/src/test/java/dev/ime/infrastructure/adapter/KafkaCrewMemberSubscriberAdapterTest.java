package dev.ime.infrastructure.adapter;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.event.Event;
import dev.ime.domain.port.outbound.BaseProjectorPort;
import dev.ime.domain.port.outbound.ExtendedProjectorPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class KafkaCrewMemberSubscriberAdapterTest {

	@Mock
	private LoggerUtil loggerUtil;	
	
	@Mock 
    private BaseProjectorPort baseProjectorPort;
	
	@Mock
    private ExtendedProjectorPort extendedProjectorPort;

	@InjectMocks
	private KafkaCrewMemberSubscriberAdapter kafkaCrewMemberSubscriberAdapter;

	private ConsumerRecord<String, Event> consumerRecord;
	private Event event;
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREWMEMBER_CAT;
	private final String eventType = GlobalConstants.CREWMEMBER_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();

	@BeforeEach
	private void setUp() {
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);		
		
	}

	@Test
	void onMessage_WithConsumerRecord_FinishOk() {
		
		consumerRecord = createConsumerRecord(eventType);
		Mockito.when(baseProjectorPort.deleteById(Mockito.any(Event.class))).thenReturn(Mono.empty());
		
		StepVerifier
		.create(kafkaCrewMemberSubscriberAdapter.onMessage(consumerRecord))
		.verifyComplete();
		
		Mockito.verify(baseProjectorPort).deleteById(Mockito.any(Event.class));
		
	}

	@Test
	void onMessage_WithBadTopic_GenerateError() {
		
		consumerRecord = createConsumerRecord("");
		
		StepVerifier
		.create(kafkaCrewMemberSubscriberAdapter.onMessage(consumerRecord))
		.verifyComplete();
		
		Mockito.verify(loggerUtil).logSevereAction(Mockito.anyString());
		
	}

	@Test
	void onMessage_ThrowEx_ManageError() {
		
		consumerRecord = createConsumerRecord(eventType);
	    RuntimeException testException = new RuntimeException(GlobalConstants.MSG_UNKNOWDATA);
	    Mockito.doThrow(testException).when(baseProjectorPort).deleteById(Mockito.any(Event.class));
	    
	    StepVerifier
		.create(kafkaCrewMemberSubscriberAdapter.onMessage(consumerRecord))
		.verifyComplete();		
		
		Mockito.verify(loggerUtil).logSevereAction(Mockito.anyString());
		Mockito.verify(baseProjectorPort).deleteById(Mockito.any(Event.class));

	}
	
	private ConsumerRecord<String, Event> createConsumerRecord(String eventType) {
		
		return new ConsumerRecord<>(
				eventType,
				1,
				1L,
				"",
				event
				);
		
	}
	
}
