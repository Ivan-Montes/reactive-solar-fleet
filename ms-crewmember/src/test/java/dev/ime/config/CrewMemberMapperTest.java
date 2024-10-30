package dev.ime.config;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.dto.CrewMemberDto;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.CrewMember;
import dev.ime.infrastructure.entity.CrewMemberJpaEntity;
import dev.ime.infrastructure.entity.EventMongoEntity;


@ExtendWith(MockitoExtension.class)
class CrewMemberMapperTest {

	@InjectMocks
	private CrewMemberMapper mapper;

	private Event event;
	private EventMongoEntity eventMongoEntity;
	private CrewMemberJpaEntity crewMemberJpaEntity;
	private CrewMember crewMember;

	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREWMEMBER_CAT;
	private final String eventType = GlobalConstants.CREWMEMBER_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();

	private final UUID crewMemberId = UUID.randomUUID();
	private final String crewMemberName = "";
	private final String crewMemberSurname = "";
	private final UUID positionId = UUID.randomUUID();
	private final UUID spacecraftId = UUID.randomUUID();
	
	@BeforeEach
	private void setUp() {
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
		eventMongoEntity = new EventMongoEntity(
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
		
		crewMember = new CrewMember(
				crewMemberId,
				crewMemberName,
				crewMemberSurname,
				positionId,
				spacecraftId);		
		
	}
	
	@Test
	void fromEventDomainToEventMongo_WithEvent_ReturnEventMongoEntity() {
		
		EventMongoEntity entity = mapper.fromEventDomainToEventMongo(event);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(entity).isNotNull(),
				()-> Assertions.assertThat(entity.getEventId()).isEqualTo(eventId),
				()-> Assertions.assertThat(entity.getEventCategory()).isEqualTo(event.getEventCategory()),
				()-> Assertions.assertThat(entity.getEventType()).isEqualTo(event.getEventType()),
				()-> Assertions.assertThat(entity.getEventTimestamp()).isEqualTo(event.getEventTimestamp()),
				()-> Assertions.assertThat(entity.getEventData()).isEqualTo(event.getEventData())
				);
	}

	@Test
	void fromEventMongoToEventDomain_WithEventMongoEntity_ReturnEvent() {		
		
		Event entity = mapper.fromEventMongoToEventDomain(eventMongoEntity);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(entity).isNotNull(),
				()-> Assertions.assertThat(entity.getEventId()).isEqualTo(eventMongoEntity.getEventId()),
				()-> Assertions.assertThat(entity.getEventCategory()).isEqualTo(eventMongoEntity.getEventCategory()),
				()-> Assertions.assertThat(entity.getEventType()).isEqualTo(eventMongoEntity.getEventType()),
				()-> Assertions.assertThat(entity.getEventTimestamp()).isEqualTo(eventMongoEntity.getEventTimestamp()),
				()-> Assertions.assertThat(entity.getEventData()).isEqualTo(eventMongoEntity.getEventData())
				);
		
	}

	@Test
	void fromJpaToDomain_WithCrewMemberJpaEntity_ReturnCrewMember() {
		
		CrewMember entity = mapper.fromJpaToDomain(crewMemberJpaEntity);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(entity).isNotNull(),
				()-> Assertions.assertThat(entity.getCrewMemberId()).isEqualTo(crewMemberJpaEntity.getCrewMemberId()),
				()-> Assertions.assertThat(entity.getCrewMemberName()).isEqualTo(crewMemberJpaEntity.getCrewMemberName()),
				()-> Assertions.assertThat(entity.getCrewMemberSurname()).isEqualTo(crewMemberJpaEntity.getCrewMemberSurname()),
				()-> Assertions.assertThat(entity.getPositionId()).isEqualTo(crewMemberJpaEntity.getPositionId()),
				()-> Assertions.assertThat(entity.getSpacecraftId()).isEqualTo(crewMemberJpaEntity.getSpacecraftId())
				);
	}

	@Test
	void fromDomainToDto_WithCrewMember_ReturnCrewMemberDto() {
		
		CrewMemberDto dto = mapper.fromDomainToDto(crewMember);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(dto).isNotNull(),
				()-> Assertions.assertThat(dto.crewMemberId()).isEqualTo(crewMember.getCrewMemberId()),
				()-> Assertions.assertThat(dto.crewMemberName()).isEqualTo(crewMember.getCrewMemberName()),
				()-> Assertions.assertThat(dto.crewMemberSurname()).isEqualTo(crewMember.getCrewMemberSurname()),
				()-> Assertions.assertThat(dto.positionId()).isEqualTo(crewMember.getPositionId()),
				()-> Assertions.assertThat(dto.spacecraftId()).isEqualTo(crewMember.getSpacecraftId())
				);
		
	}
	
}
