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

import dev.ime.application.dto.PositionDto;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Position;
import dev.ime.infrastructure.entity.EventMongoEntity;
import dev.ime.infrastructure.entity.PositionJpaEntity;


@ExtendWith(MockitoExtension.class)
class PositionMapperTest {

	@InjectMocks
	private PositionMapper positionMapper;
	
	private Event event;
	private EventMongoEntity eventMongoEntity;
	private PositionJpaEntity positionJpaEntity;
	private Position position;
	
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.POSITION_CAT;
	private final String eventType = GlobalConstants.POSITION_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();
	
	private final UUID positionId = UUID.randomUUID();
	private final String positionName = "";
	private final String positionDescription = "";
	
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
		
		positionJpaEntity = new PositionJpaEntity(
				positionId,
				positionName,
				positionDescription);		
		
		position = new Position(
				positionId,
				positionName,
				positionDescription);
		
	}
	
	@Test
	void fromEventDomainToEventMongo_WithEvent_ReturnEventMongoEntity() {
		
		EventMongoEntity entity = positionMapper.fromEventDomainToEventMongo(event);
		
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
		
		Event entity = positionMapper.fromEventMongoToEventDomain(eventMongoEntity);
		
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
	void fromPositionJpaToPositionDomain_WithPositionJpaEntity_ReturnPosition() {
		
		Position entity = positionMapper.fromJpaToDomain(positionJpaEntity);

		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(entity).isNotNull(),
				()-> Assertions.assertThat(entity.getPositionId()).isEqualTo(positionJpaEntity.getPositionId()),
				()-> Assertions.assertThat(entity.getPositionName()).isEqualTo(positionJpaEntity.getPositionName()),
				()-> Assertions.assertThat(entity.getPositionDescription()).isEqualTo(positionJpaEntity.getPositionDescription())
				);
	}

	@Test
	void fromDomainToPositionDto_WithPosition_ReturnPositionDto() {
		
		PositionDto entity = positionMapper.fromDomainToDto(position);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(entity).isNotNull(),
				()-> Assertions.assertThat(entity.positionId()).isEqualTo(position.getPositionId()),
				()-> Assertions.assertThat(entity.positionName()).isEqualTo(position.getPositionName()),
				()-> Assertions.assertThat(entity.positionDescription()).isEqualTo(position.getPositionDescription())
				);
		
	}

}
