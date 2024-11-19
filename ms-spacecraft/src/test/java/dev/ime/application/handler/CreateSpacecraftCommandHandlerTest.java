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
import dev.ime.application.usecase.CreateSpacecraftCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Spacecraft;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreateSpacecraftCommandHandlerTest {

	@Mock
	private EventWriteRepositoryPort eventWriteRepositoryPort;
	
	@Mock
	private ObjectMapper objectMapper;
	
	@Mock
	private ReadRepositoryPort<Spacecraft> readRepositoryPort;

	@Mock
	private RedisCheckerEntityPort redisCheckerEntityAdapter;

	@InjectMocks
	private CreateSpacecraftCommandHandler createSpacecraftCommandHandler;	

	private Event event;
	private CreateSpacecraftCommand createCommand;	

	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.SPACECRAFT_CAT;
	private final String eventType = GlobalConstants.SPACECRAFT_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private Map<String, Object> eventData = new HashMap<>();		

	private final UUID spacecraftId = UUID.randomUUID();
	private final String spacecraftName = "";
	private final UUID shipclassId = UUID.randomUUID();

	@BeforeEach
	private void setUp() {

		eventData = new HashMap<>();
		eventData.put(GlobalConstants.SPACECRAFT_ID, spacecraftId.toString());
		eventData.put(GlobalConstants.SPACECRAFT_NAME, spacecraftName);
		eventData.put(GlobalConstants.SHIPCLASS_ID, shipclassId.toString());
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);			
		
		createCommand = new CreateSpacecraftCommand(
				spacecraftId,
				spacecraftName,
				shipclassId);
		
	}	

	@SuppressWarnings("unchecked")
	@Test
	void handle_WithCommand_ReturnMonoEvent() {
		
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(redisCheckerEntityAdapter.existsById(Mockito.any(UUID.class))).thenReturn(Mono.just(true));
		Mockito.when(objectMapper.convertValue(Mockito.any(CreateSpacecraftCommand.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));

		Mono<Event> result = createSpacecraftCommandHandler.handle(createCommand);
		
		StepVerifier.create(result)
		.expectNext(event)
		.verifyComplete();
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());
		Mockito.verify(redisCheckerEntityAdapter).existsById(Mockito.any(UUID.class));
		Mockito.verify(objectMapper).convertValue(Mockito.any(CreateSpacecraftCommand.class), Mockito.any(TypeReference.class));
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));

	}

	@Test
	void handle_WithRepeatedName_PropagateError() {
		
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.just(new Spacecraft()));

		Mono<Event> result = createSpacecraftCommandHandler.handle(createCommand);
		
		StepVerifier.create(result)
		.expectError(UniqueValueException.class)
		.verify();
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());

	}

	@Test
	void handle_WithShipclassNotExists_PropagateError() {
		
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(redisCheckerEntityAdapter.existsById(Mockito.any(UUID.class))).thenReturn(Mono.just(false));

		Mono<Event> result = createSpacecraftCommandHandler.handle(createCommand);
		
		StepVerifier.create(result)
		.expectError(ResourceNotFoundException.class)
		.verify();
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());
		Mockito.verify(redisCheckerEntityAdapter).existsById(Mockito.any(UUID.class));

	}

}
