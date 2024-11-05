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

import dev.ime.application.usecase.DeleteCrewMemberCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.CrewMember;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DeleteCrewMemberCommandHandlerTest {

	@Mock
	private EventWriteRepositoryPort eventWriteRepositoryPort;
	
	@Mock
	private ReadRepositoryPort<CrewMember> readRepositoryPort;
	
	@Mock
	private ObjectMapper objectMapper;
	
	@InjectMocks
	private DeleteCrewMemberCommandHandler deleteCrewMemberCommandHandler;
	
	private Event event;
	private DeleteCrewMemberCommand deleteCommand;
	
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREWMEMBER_CAT;
	private final String eventType = GlobalConstants.CREWMEMBER_DELETED;
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

		deleteCommand = new DeleteCrewMemberCommand(
				crewMemberId);
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void handle_WithId_ReturnCreatedEvent() {
		
		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(new CrewMember()));
		Mockito.when(objectMapper.convertValue(Mockito.any(DeleteCrewMemberCommand.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));
		
		Mono<Event> result = deleteCrewMemberCommandHandler.handle(deleteCommand);
		
		StepVerifier
		.create(result)
		.expectNext(event)
		.verifyComplete();	
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(objectMapper).convertValue(Mockito.any(DeleteCrewMemberCommand.class), Mockito.any(TypeReference.class));
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));
		
	}

}
