package dev.ime.infrastructure.adapter;

import static org.mockito.Mockito.times;

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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class KafkaShipclassSubscriberAdapterTest {

	@Mock
	private LoggerUtil loggerUtil;
	
	@Mock
    private BaseProjectorPort baseProjectorPort;
	
	@InjectMocks
	private KafkaShipclassSubscriberAdapter kafkaShipclassSubscriberAdapter;
	
	private ConsumerRecord<String, Event> consumerRecord;
	private Event event;
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.SHIPCLASS_CAT;
	private final String eventType = GlobalConstants.SHIPCLASS_DELETED;
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
	void onMessage_WithConsumerRecord_ReceiveData() {
		
		consumerRecord = createConsumerRecord(eventType);
		Mockito.when(baseProjectorPort.deleteById(Mockito.any(Event.class))).thenReturn(Mono.empty());

		StepVerifier
		.create(kafkaShipclassSubscriberAdapter.onMessage(consumerRecord))
		.verifyComplete();
		
		Mockito.verify(baseProjectorPort).deleteById(Mockito.any(Event.class));
	}

	@Test
	void onMessage_WithUnknowTopic_ReceiveData() {
		
		consumerRecord = createConsumerRecord(GlobalConstants.MSG_UNKNOWDATA);

		StepVerifier
		.create(kafkaShipclassSubscriberAdapter.onMessage(consumerRecord))
		.verifyComplete();
		
		Mockito.verify(loggerUtil, times(1)).logSevereAction(Mockito.anyString());

	}

	@Test
	void onMessage_ThrowEx_ManageError() {
		
		consumerRecord = createConsumerRecord(eventType);
	    RuntimeException testException = new RuntimeException("Test error");
	    Mockito.doThrow(testException).when(baseProjectorPort).deleteById(Mockito.any(Event.class));

		StepVerifier
		.create(kafkaShipclassSubscriberAdapter.onMessage(consumerRecord))
		.verifyComplete();
		
		Mockito.verify(loggerUtil, times(2)).logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
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
