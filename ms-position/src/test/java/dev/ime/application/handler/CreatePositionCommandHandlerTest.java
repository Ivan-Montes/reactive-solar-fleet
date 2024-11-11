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

import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecase.CreatePositionCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Position;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreatePositionCommandHandlerTest {

	@Mock
	private EventWriteRepositoryPort eventWriteRepositoryPort;
	
	@Mock
	private ObjectMapper objectMapper;
	
	@Mock
	private ReadRepositoryPort<Position> readRepositoryPort;
	
	@InjectMocks
	private CreatePositionCommandHandler createPositionCommandHandler;
	
	private CreatePositionCommand createCommand;
	private Event event;
	
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.POSITION_CAT;
	private final String eventType = GlobalConstants.POSITION_CREATED;
	private final Instant eventTimestamp = Instant.now();
	private Map<String, Object> eventData;

	private final UUID positionId = UUID.randomUUID();
	private final String positionName = "";
	private final String positionDescription = "";

	@BeforeEach
	private void setUp() {		
		
		eventData = new HashMap<>();
		eventData.put(GlobalConstants.POSITION_ID, positionId.toString());
		eventData.put(GlobalConstants.POSITION_NAME, positionName);
		eventData.put(GlobalConstants.POSITION_DESC, positionDescription);		

		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);

		createCommand = new CreatePositionCommand(
				positionId,
				positionName,
				positionDescription);
		
	}
		
	@SuppressWarnings("unchecked")
	@Test
	void handle_WithCommand_ReturnMonoEvent() {
		
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(objectMapper.convertValue(Mockito.any(CreatePositionCommand.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));

		Mono<Event> result = createPositionCommandHandler.handle(createCommand);
		
		StepVerifier.create(result)
		.expectNext(event)
		.verifyComplete();
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());
		Mockito.verify(objectMapper).convertValue(Mockito.any(CreatePositionCommand.class), Mockito.any(TypeReference.class));
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));

	}

	@Test
	void handle_WithRepeatedName_PropagateError() {
		
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.just(new Position()));

		Mono<Event> result = createPositionCommandHandler.handle(createCommand);
		
		StepVerifier.create(result)
		.expectError(UniqueValueException.class)
		.verify();
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());

	}
}
