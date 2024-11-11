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
import dev.ime.application.usecase.UpdatePositionCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Position;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UpdatePositionCommandHandlerTest {

	@Mock
	private EventWriteRepositoryPort eventWriteRepositoryPort;
	
	@Mock
	private ObjectMapper objectMapper;
	
	@Mock
	private ReadRepositoryPort<Position> readRepositoryPort;
	
	@InjectMocks
	private UpdatePositionCommandHandler updatePositionCommandHandler;
	
	private UpdatePositionCommand updatePositionCommand;
	private Event event;
	private Position position;
	private Position position1;
	
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.POSITION_CAT;
	private final String eventType = GlobalConstants.POSITION_UPDATED;
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

		updatePositionCommand = new UpdatePositionCommand(
				positionId,
				positionName,
				positionDescription);		

		position = new Position(
				positionId,
				positionName,
				positionDescription);
		
		position1 = new Position(
				UUID.randomUUID(),
				positionName,
				positionDescription);
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void handle_WithCommand_ReturnMonoEvent() {

		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(position));
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.just(position));
		Mockito.when(objectMapper.convertValue(Mockito.any(UpdatePositionCommand.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));		
		
		Mono<Event> result = updatePositionCommandHandler.handle(updatePositionCommand);

		StepVerifier.create(result)
		.expectNext(event)
		.verifyComplete();
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());
		Mockito.verify(objectMapper).convertValue(Mockito.any(UpdatePositionCommand.class), Mockito.any(TypeReference.class));
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));
	
	}
	
	@Test
	void handle_WithIdNotFound_PropagateError() {
		
		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.empty());

		Mono<Event> result = updatePositionCommandHandler.handle(updatePositionCommand);

		StepVerifier.create(result)
		.expectError(ResourceNotFoundException.class)
		.verify();
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));

	}	

	@Test
	void handle_WithNameUsed_PropagateError() {

		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(position));
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.just(position1));
		
		Mono<Event> result = updatePositionCommandHandler.handle(updatePositionCommand);

		StepVerifier.create(result)
		.expectError(UniqueValueException.class)
		.verify();
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());
		
	}

}
