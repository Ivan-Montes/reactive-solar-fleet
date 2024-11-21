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

import dev.ime.application.dto.SpacecraftDto;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Spacecraft;
import dev.ime.infrastructure.entity.EventMongoEntity;
import dev.ime.infrastructure.entity.SpacecraftJpaEntity;

@ExtendWith(MockitoExtension.class)
class SpacecraftMapperTest {

	@InjectMocks
	private SpacecraftMapper mapper;	

	private Event event;
	private EventMongoEntity eventMongoEntity;
	private SpacecraftJpaEntity spacecraftJpaEntity;
	private Spacecraft spacecraft;

	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.SPACECRAFT_CAT;
	private final String eventType = GlobalConstants.SPACECRAFT_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();	

	private final UUID spacecraftId = UUID.randomUUID();
	private final String spacecraftName = "";
	private final UUID shipclassId = UUID.randomUUID();
	
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
		
		spacecraftJpaEntity = new SpacecraftJpaEntity(
				spacecraftId,
				spacecraftName,
				shipclassId);		
		
		spacecraft = new Spacecraft(
				spacecraftId,
				spacecraftName,
				shipclassId);
		
	}

	@Test
	void fromEventDomainToEventMongo_WithEvent_ReturnEventMongoEntity() {
		
		EventMongoEntity entity = mapper.fromEventDomainToEventMongo(event);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(entity).isNotNull(),
				()-> Assertions.assertThat(entity.getEventId()).isEqualTo(event.getEventId()),
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
	void fromJpaToDomain_WithJpaEntity_ReturnDomainEntity() {
		
		Spacecraft entity = mapper.fromJpaToDomain(spacecraftJpaEntity);

		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(entity).isNotNull(),
				()-> Assertions.assertThat(entity.getSpacecraftId()).isEqualTo(spacecraftJpaEntity.getSpacecraftId()),
				()-> Assertions.assertThat(entity.getSpacecraftName()).isEqualTo(spacecraftJpaEntity.getSpacecraftName()),
				()-> Assertions.assertThat(entity.getShipclassId()).isEqualTo(spacecraftJpaEntity.getShipclassId())
				);
	}

	@Test
	void fromDomainToDto_WithDomainEntity_ReturnDto() {
		
		SpacecraftDto entity = mapper.fromDomainToDto(spacecraft);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(entity).isNotNull(),
				()-> Assertions.assertThat(entity.spacecraftId()).isEqualTo(spacecraftJpaEntity.getSpacecraftId()),
				()-> Assertions.assertThat(entity.spacecraftName()).isEqualTo(spacecraftJpaEntity.getSpacecraftName()),
				()-> Assertions.assertThat(entity.shipclassId()).isEqualTo(spacecraftJpaEntity.getShipclassId())
				);
		
	}

}
