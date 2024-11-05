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
import dev.ime.application.usecase.UpdateCrewMemberCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.CrewMember;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UpdateCrewMemberCommandHandlerTest {

	@Mock
	private EventWriteRepositoryPort eventWriteRepositoryPort;
	
	@Mock
	private ObjectMapper objectMapper;
	
	@Mock
	private RedisCheckerEntityPort redisCheckerEntityPort;
	
	@Mock
	private ReadRepositoryPort<CrewMember> readRepositoryPort;

	@InjectMocks
	private UpdateCrewMemberCommandHandler updateCrewMemberCommandHandler;

	private Event event;
	private UpdateCrewMemberCommand updateCommand;
	
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREWMEMBER_CAT;
	private final String eventType = GlobalConstants.CREWMEMBER_UPDATED;
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

		updateCommand = new UpdateCrewMemberCommand(
				crewMemberId,
				crewMemberName,
				crewMemberSurname,
				positionId,
				spacecraftId);
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void handle_WithCommandOk_ReturnCreatedEvent() {
		
		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(new CrewMember()));
		Mockito.when(redisCheckerEntityPort.existsPosition(Mockito.any(UUID.class))).thenReturn(Mono.just(true));
		Mockito.when(redisCheckerEntityPort.existsSpacecraft(Mockito.any(UUID.class))).thenReturn(Mono.just(true));
		Mockito.when(objectMapper.convertValue(Mockito.any(UpdateCrewMemberCommand.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));
		
		Mono<Event> result = updateCrewMemberCommandHandler.handle(updateCommand);
		
		StepVerifier
		.create(result)
		.expectNext(event)
		.verifyComplete();		
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(redisCheckerEntityPort).existsPosition(Mockito.any(UUID.class));
		Mockito.verify(redisCheckerEntityPort).existsSpacecraft(Mockito.any(UUID.class));
		Mockito.verify(objectMapper).convertValue(Mockito.any(UpdateCrewMemberCommand.class), Mockito.any(TypeReference.class));
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));			
	}

	@SuppressWarnings("unchecked")
	@Test
	void handle_WithPositionAndSpacecraftIdNull_ReturnEventCreated() {
		
		updateCommand = new UpdateCrewMemberCommand(
				crewMemberId,
				crewMemberName,
				crewMemberSurname,
				null,
				null);
		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(new CrewMember()));
		Mockito.when(objectMapper.convertValue(Mockito.any(UpdateCrewMemberCommand.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));
		
		Mono<Event> result = updateCrewMemberCommandHandler.handle(updateCommand);
		
		StepVerifier
		.create(result)
		.expectNext(event)
		.verifyComplete();		
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(objectMapper).convertValue(Mockito.any(UpdateCrewMemberCommand.class), Mockito.any(TypeReference.class));
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));	
		
	}

	@Test
	void handle_WithNotFoundEntity_PropagateErrorResourceNotFoundException() {
		
		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.empty());
		
		Mono<Event> result = updateCrewMemberCommandHandler.handle(updateCommand);
		
		StepVerifier
		.create(result)
		.expectError(ResourceNotFoundException.class)
		.verify();
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		
	}
	
}
