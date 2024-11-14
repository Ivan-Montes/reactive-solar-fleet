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

import dev.ime.application.dto.ShipclassDto;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Shipclass;
import dev.ime.infrastructure.entity.EventMongoEntity;
import dev.ime.infrastructure.entity.ShipclassJpaEntity;

@ExtendWith(MockitoExtension.class)
class ShipclassMapperTest {

	@InjectMocks
	private ShipclassMapper mapper;	

	private Event event;
	private EventMongoEntity eventMongoEntity;
	private ShipclassJpaEntity shipclassJpaEntity;
	private Shipclass shipclass;

	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.SHIPCLASS_CAT;
	private final String eventType = GlobalConstants.SHIPCLASS_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();	

	private final UUID shipclassId = UUID.randomUUID();
	private final String shipclassName = "";
	private final String shipclassDescription = "";
	
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
		
		shipclassJpaEntity = new ShipclassJpaEntity(
				shipclassId,
				shipclassName,
				shipclassDescription);		
		
		shipclass = new Shipclass(
				shipclassId,
				shipclassName,
				shipclassDescription);
		
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
		
		Shipclass entity = mapper.fromJpaToDomain(shipclassJpaEntity);

		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(entity).isNotNull(),
				()-> Assertions.assertThat(entity.getShipclassId()).isEqualTo(shipclassJpaEntity.getShipclassId()),
				()-> Assertions.assertThat(entity.getShipclassName()).isEqualTo(shipclassJpaEntity.getShipclassName()),
				()-> Assertions.assertThat(entity.getShipclassDescription()).isEqualTo(shipclassJpaEntity.getShipclassDescription())
				);
	}

	@Test
	void fromDomainToDto_WithDomainEntity_ReturnDto() {
		
		ShipclassDto entity = mapper.fromDomainToDto(shipclass);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(entity).isNotNull(),
				()-> Assertions.assertThat(entity.shipclassId()).isEqualTo(shipclass.getShipclassId()),
				()-> Assertions.assertThat(entity.shipclassName()).isEqualTo(shipclass.getShipclassName()),
				()-> Assertions.assertThat(entity.shipclassDescription()).isEqualTo(shipclass.getShipclassDescription())
				);
		
	}

}
