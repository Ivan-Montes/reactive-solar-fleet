package dev.ime.application.handler;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecase.UpdateShipclassCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Shipclass;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UpdateShipclassCommandHandlerTest {

	@Mock
	private EventWriteRepositoryPort eventWriteRepositoryPort;
	
	@Mock
	private ObjectMapper objectMapper;
	
	@Mock
	private ReadRepositoryPort<Shipclass> readRepositoryPort;
	
	@InjectMocks
	private UpdateShipclassCommandHandler updateShipclassCommandHandler;
	
	private UpdateShipclassCommand updateCommand;	
	private Event event;	
	private Shipclass shipclass0;
	private Shipclass shipclass1;
	
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.SHIPCLASS_CAT;
	private final String eventType = GlobalConstants.SHIPCLASS_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private Map<String, Object> eventData = new HashMap<>();	

	private final UUID shipclassId0 = UUID.randomUUID();
	private final UUID shipclassId1 = UUID.randomUUID();
	private final String shipclassName = "";
	private final String shipclassDescription = "";
	
	@BeforeEach
	private void setUp() {

		eventData = new HashMap<>();
		eventData.put(GlobalConstants.SHIPCLASS_ID, shipclassId0.toString());
		eventData.put(GlobalConstants.SHIPCLASS_NAME, shipclassName);
		eventData.put(GlobalConstants.SHIPCLASS_DESC, shipclassDescription);
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
		updateCommand = new UpdateShipclassCommand(
				shipclassId0,
				shipclassName,
				shipclassDescription);				

		shipclass0 = new Shipclass(
				shipclassId0,
				shipclassName,
				shipclassDescription);

		shipclass1 = new Shipclass(
				shipclassId1,
				shipclassName,
				shipclassDescription);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void handle_WithCommand_ReturnMonoEvent() {

		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(shipclass0));
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.just(shipclass0));
		Mockito.when(objectMapper.convertValue(Mockito.any(UpdateShipclassCommand.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));		
		
		Mono<Event> result = updateShipclassCommandHandler.handle(updateCommand);

		StepVerifier.create(result)
		.expectNext(event)
		.verifyComplete();
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());
		Mockito.verify(objectMapper).convertValue(Mockito.any(UpdateShipclassCommand.class), Mockito.any(TypeReference.class));
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));
	
	}
	
	@Test
	void handle_WithIdNotFound_PropagateError() {
		
		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.empty());

		Mono<Event> result = updateShipclassCommandHandler.handle(updateCommand);

		StepVerifier.create(result)
		.expectError(ResourceNotFoundException.class)
		.verify();
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));

	}
	

	@Test
	void handle_WithNameUsed_PropagateError() {

		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(shipclass0));
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.just(shipclass1));
		
		Mono<Event> result = updateShipclassCommandHandler.handle(updateCommand);

		StepVerifier.create(result)
		.expectError(UniqueValueException.class)
		.verify();
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());
		
	}

}
