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

import dev.ime.application.usecase.CreateCrewMemberCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreateCrewMemberCommandHandlerTest {

	@Mock
	private EventWriteRepositoryPort eventWriteRepositoryPort;
	
	@Mock
	private ObjectMapper objectMapper;
	
	@Mock
	private RedisCheckerEntityPort redisCheckerEntityPort;
	
	@InjectMocks
	private CreateCrewMemberCommandHandler createCrewMemberCommandHandler;
	
	private Event event;
	private CreateCrewMemberCommand createCommand;
	
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREWMEMBER_CAT;
	private final String eventType = GlobalConstants.CREWMEMBER_CREATED;
	private final Instant eventTimestamp = Instant.now();
	private Map<String, Object> eventData;
	
	private final UUID crewMemberId = UUID.randomUUID();
	private final String crewMemberName = "";
	private final String crewMemberSurname = "";
	private final UUID positionId = UUID.randomUUID();
	private final UUID spacecraftId = UUID.randomUUID();
	
	@BeforeEach
	private void setUp() {		
		
		eventData = new HashMap<>();
		eventData.put(GlobalConstants.CREWMEMBER_ID, crewMemberId.toString());
		eventData.put(GlobalConstants.CREWMEMBER_NAME, crewMemberName);
		eventData.put(GlobalConstants.CREWMEMBER_SURNAME, crewMemberSurname);
		eventData.put(GlobalConstants.POSITION_ID, positionId.toString());
		eventData.put(GlobalConstants.SPACECRAFT_ID, spacecraftId.toString());		

		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);

		createCommand = new CreateCrewMemberCommand(
				crewMemberId,
				crewMemberName,
				crewMemberSurname,
				positionId,
				spacecraftId);
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void handle_WithCommandOk_ReturnEventCreated() {
		
		Mockito.when(redisCheckerEntityPort.existsPosition(Mockito.any(UUID.class))).thenReturn(Mono.just(true));
		Mockito.when(redisCheckerEntityPort.existsSpacecraft(Mockito.any(UUID.class))).thenReturn(Mono.just(true));
		Mockito.when(objectMapper.convertValue(Mockito.any(CreateCrewMemberCommand.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));
		
		Mono<Event> result = createCrewMemberCommandHandler.handle(createCommand);

		StepVerifier
		.create(result)
		.expectNext(event)
		.verifyComplete();		
		Mockito.verify(redisCheckerEntityPort).existsPosition(Mockito.any(UUID.class));
		Mockito.verify(redisCheckerEntityPort).existsSpacecraft(Mockito.any(UUID.class));
		Mockito.verify(objectMapper).convertValue(Mockito.any(CreateCrewMemberCommand.class), Mockito.any(TypeReference.class));
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));		
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void handle_WithPositionAndSpacecraftIdNull_ReturnEventCreated() {
		
		createCommand = new CreateCrewMemberCommand(
				crewMemberId,
				crewMemberName,
				crewMemberSurname,
				null,
				null);
		Mockito.when(objectMapper.convertValue(Mockito.any(CreateCrewMemberCommand.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));
		
		Mono<Event> result = createCrewMemberCommandHandler.handle(createCommand);

		StepVerifier
		.create(result)
		.expectNext(event)
		.verifyComplete();		
		Mockito.verify(objectMapper).convertValue(Mockito.any(CreateCrewMemberCommand.class), Mockito.any(TypeReference.class));
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));		
		
	}

}
