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

import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecase.DeleteShipclassCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Shipclass;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DeleteShipclassCommandHandlerTest {

	@Mock
	private EventWriteRepositoryPort eventWriteRepositoryPort;
	
	@Mock
	private ObjectMapper objectMapper;
	
	@Mock
	private ReadRepositoryPort<Shipclass> readRepositoryPort;
	
	@Mock
	private RedisCheckerEntityPort redisDbCheckerEntityAdapter;
	
	@InjectMocks
	private DeleteShipclassCommandHandler deleteShipclassCommandHandler;

	private Event event;
	private DeleteShipclassCommand deleteCommand;
	
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.SHIPCLASS_CAT;
	private final String eventType = GlobalConstants.SHIPCLASS_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private Map<String, Object> eventData = new HashMap<>();	

	private final UUID shipclassId = UUID.randomUUID();
	private final String shipclassName = "";
	private final String shipclassDescription = "";
	
	@BeforeEach
	private void setUp() {

		eventData = new HashMap<>();
		eventData.put(GlobalConstants.SHIPCLASS_ID, shipclassId.toString());
		eventData.put(GlobalConstants.SHIPCLASS_NAME, shipclassName);
		eventData.put(GlobalConstants.SHIPCLASS_DESC, shipclassDescription);
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
		deleteCommand = new DeleteShipclassCommand(
				shipclassId);				
				
	}

	@SuppressWarnings("unchecked")
	@Test
	void handle_WithCommand_ReturnMonoEvent() {
		
		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(new Shipclass()));
		Mockito.when(redisDbCheckerEntityAdapter.existsAnyByShipclassId(Mockito.any(UUID.class))).thenReturn(Mono.just(false));
		Mockito.when(objectMapper.convertValue(Mockito.any(DeleteShipclassCommand.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));
		
		Mono<Event> result = deleteShipclassCommandHandler.handle(deleteCommand);
		
		StepVerifier.create(result)
		.expectNext(event)
		.verifyComplete();
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(redisDbCheckerEntityAdapter).existsAnyByShipclassId(Mockito.any(UUID.class));
		Mockito.verify(objectMapper).convertValue(Mockito.any(DeleteShipclassCommand.class), Mockito.any(TypeReference.class));
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));
	
	}
	
	@Test
	void handle_WithShipclassNoExists_PropagateError() {
		
		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.empty());

		Mono<Event> result = deleteShipclassCommandHandler.handle(deleteCommand);

		StepVerifier.create(result)
		.expectError(ResourceNotFoundException.class)
		.verify();
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));

	}

	@Test
	void handle_WithShipclassInSpacecraft_PropagateError() {		

		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(new Shipclass()));
		Mockito.when(redisDbCheckerEntityAdapter.existsAnyByShipclassId(Mockito.any(UUID.class))).thenReturn(Mono.just(true));

		Mono<Event> result = deleteShipclassCommandHandler.handle(deleteCommand);

		StepVerifier.create(result)
		.expectError(EntityAssociatedException.class)
		.verify();
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(redisDbCheckerEntityAdapter).existsAnyByShipclassId(Mockito.any(UUID.class));

	}

}
