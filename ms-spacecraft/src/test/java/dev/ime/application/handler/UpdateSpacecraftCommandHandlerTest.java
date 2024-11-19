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
import dev.ime.application.usecase.UpdateSpacecraftCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Spacecraft;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UpdateSpacecraftCommandHandlerTest {

	@Mock
	private EventWriteRepositoryPort eventWriteRepositoryPort;
	
	@Mock
	private ObjectMapper objectMapper;
	
	@Mock
	private ReadRepositoryPort<Spacecraft> readRepositoryPort;

	@Mock
	private RedisCheckerEntityPort redisCheckerEntityAdapter;

	@InjectMocks
	private UpdateSpacecraftCommandHandler updateSpacecraftCommandHandler;
	
	private Event event;
	private UpdateSpacecraftCommand updateCommand;	
	private Spacecraft spacecraft0;
	private Spacecraft spacecraft1;

	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.SPACECRAFT_CAT;
	private final String eventType = GlobalConstants.SPACECRAFT_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private Map<String, Object> eventData = new HashMap<>();		

	private final UUID spacecraftId0= UUID.randomUUID();
	private final UUID spacecraftId1 = UUID.randomUUID();
	private final String spacecraftName = "";
	private final UUID shipclassId = UUID.randomUUID();

	@BeforeEach
	private void setUp() {

		eventData = new HashMap<>();
		eventData.put(GlobalConstants.SPACECRAFT_ID, spacecraftId0.toString());
		eventData.put(GlobalConstants.SPACECRAFT_NAME, spacecraftName);
		eventData.put(GlobalConstants.SHIPCLASS_ID, shipclassId.toString());
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);			
		
		updateCommand = new UpdateSpacecraftCommand(
				spacecraftId0,
				spacecraftName,
				shipclassId);
		
		spacecraft0 = new Spacecraft(
				spacecraftId0,
				spacecraftName,
				shipclassId);
		
		spacecraft1 = new Spacecraft();
		spacecraft1.setSpacecraftId(spacecraftId1);
		spacecraft1.setSpacecraftName(spacecraftName);
		spacecraft1.setSpacecraftId(shipclassId);
		
	}	

	@SuppressWarnings("unchecked")
	@Test
	void handle_WithCommand_ReturnMonoEvent() {

		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(spacecraft0));
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.just(spacecraft0));
		Mockito.when(redisCheckerEntityAdapter.existsById(Mockito.any(UUID.class))).thenReturn(Mono.just(true));
		Mockito.when(objectMapper.convertValue(Mockito.any(UpdateSpacecraftCommand.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));

		Mono<Event> result = updateSpacecraftCommandHandler.handle(updateCommand);
		
		StepVerifier.create(result)
		.expectNext(event)
		.verifyComplete();
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());
		Mockito.verify(redisCheckerEntityAdapter).existsById(Mockito.any(UUID.class));
		Mockito.verify(objectMapper).convertValue(Mockito.any(UpdateSpacecraftCommand.class), Mockito.any(TypeReference.class));
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));

	}

	@Test
	void handle_WithIdNotFound_PropagateError() {
		
		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.empty());

		Mono<Event> result = updateSpacecraftCommandHandler.handle(updateCommand);
		
		StepVerifier.create(result)
		.expectError(ResourceNotFoundException.class)
		.verify();
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));

	}

	@Test
	void handle_WithNameUsed_PropagateError() {

		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(spacecraft0));
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.just(spacecraft1));
		
		Mono<Event> result = updateSpacecraftCommandHandler.handle(updateCommand);

		StepVerifier.create(result)
		.expectError(UniqueValueException.class)
		.verify();
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());
		
	}

	@Test
	void handle_WithShipclassIdNotExists_PropagateError() {

		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(spacecraft0));
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(redisCheckerEntityAdapter.existsById(Mockito.any(UUID.class))).thenReturn(Mono.just(false));

		Mono<Event> result = updateSpacecraftCommandHandler.handle(updateCommand);
		
		StepVerifier.create(result)
		.expectError(ResourceNotFoundException.class)
		.verify();
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());
		Mockito.verify(redisCheckerEntityAdapter).existsById(Mockito.any(UUID.class));
		
	}
	
}
