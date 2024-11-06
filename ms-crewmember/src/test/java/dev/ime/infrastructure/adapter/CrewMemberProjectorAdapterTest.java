package dev.ime.infrastructure.adapter;


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
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;

import dev.ime.application.exception.CreateJpaEntityException;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.event.Event;
import dev.ime.infrastructure.entity.CrewMemberJpaEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CrewMemberProjectorAdapterTest {

	@Mock
	private LoggerUtil loggerUtil;
	
	@Mock
	private R2dbcEntityTemplate r2dbcTemplate;
	
	@InjectMocks
	private CrewMemberProjectorAdapter crewMemberProjectorAdapter;

	private Event event;
	private CrewMemberJpaEntity crewMemberJpaEntity;
	
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREWMEMBER_CAT;
	private final String eventType = GlobalConstants.CREWMEMBER_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private Map<String, Object> eventData;

	private final UUID crewMemberId = UUID.randomUUID();
	private final String crewMemberName = "Walter";
	private final String crewMemberSurname = "Kovacs";
	private final UUID positionId = UUID.randomUUID();
	private final UUID spacecraftId = UUID.randomUUID();
	
	@BeforeEach
	private void setUp() {
		
		eventData = new HashMap<>();
		eventData.put(GlobalConstants.CREWMEMBER_ID, positionId.toString());
		eventData.put(GlobalConstants.CREWMEMBER_NAME, crewMemberName);
		eventData.put(GlobalConstants.CREWMEMBER_SURNAME, crewMemberSurname);
		eventData.put(GlobalConstants.POSITION_ID, positionId);
		eventData.put(GlobalConstants.SPACECRAFT_ID, spacecraftId);
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);

		crewMemberJpaEntity = new CrewMemberJpaEntity(
				crewMemberId,
				crewMemberName,
				crewMemberSurname,
				positionId,
				spacecraftId);
		
	}
	
	@Test
	void create_WithEvent_InsertData() {
		
		Mockito.when(r2dbcTemplate.insert(Mockito.any(CrewMemberJpaEntity.class))).thenReturn(Mono.just(crewMemberJpaEntity));
		
		StepVerifier
		.create(crewMemberProjectorAdapter.create(event))
		.verifyComplete();
		
		Mockito.verify(r2dbcTemplate).insert(Mockito.any(CrewMemberJpaEntity.class));
		Mockito.verify(loggerUtil, Mockito.times(4)).logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

	}

	@Test
	void create_WithBadEvenData_ThrowError() {
		
		eventData.put(GlobalConstants.CREWMEMBER_NAME, "");
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);		

		StepVerifier
		.create(crewMemberProjectorAdapter.create(event))
		.expectError(CreateJpaEntityException.class)
		.verify();
		
		Mockito.verify(loggerUtil, Mockito.times(3)).logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

	}

	@Test
	void update_WithEvent_UpdateDb(){
		
		Mockito.when(r2dbcTemplate.update(Mockito.any(Query.class), Mockito.any(Update.class), Mockito.any(Class.class))).thenReturn(Mono.just(1L));
				
		StepVerifier
		.create(crewMemberProjectorAdapter.update(event))
		.verifyComplete();
		
		Mockito.verify(r2dbcTemplate).update(Mockito.any(Query.class), Mockito.any(Update.class), Mockito.any(Class.class));
		Mockito.verify(loggerUtil, Mockito.times(4)).logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

	}

	@Test
	void delete_WithEvent_DeletePosition() {
		
		Mockito.when(r2dbcTemplate.delete(Mockito.any(Query.class),Mockito.any(Class.class))).thenReturn(Mono.just(1L));

		StepVerifier
		.create(crewMemberProjectorAdapter.deleteById(event))
		.verifyComplete();
		
		Mockito.verify(r2dbcTemplate).delete(Mockito.any(Query.class),Mockito.any(Class.class));
		Mockito.verify(loggerUtil, Mockito.never()).logSevereAction(Mockito.anyString());
		 
	}
	
}
