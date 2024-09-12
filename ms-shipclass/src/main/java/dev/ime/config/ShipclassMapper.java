package dev.ime.config;

import org.springframework.stereotype.Component;

import dev.ime.application.dto.ShipclassDto;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Shipclass;
import dev.ime.infrastructure.entity.EventMongoEntity;
import dev.ime.infrastructure.entity.ShipclassJpaEntity;

@Component
public class ShipclassMapper {

	public EventMongoEntity fromEventDomainToEventMongo(Event event) {
	
		return EventMongoEntity.builder()
				.eventId(event.getEventId())
				.eventCategory(event.getEventCategory())
				.eventType(event.getEventType())
				.eventTimestamp(event.getEventTimestamp())
				.eventData(event.getEventData())
				.build();
		
	}
	
	public Event fromEventMongoToEventDomain(EventMongoEntity entity) {
		
		return new Event(
				entity.getEventId(),
				entity.getEventCategory(),
				entity.getEventType(),
				entity.getEventTimestamp(),
				entity.getEventData()				
				);
				
	}
	
	public Shipclass fromJpaToDomain(ShipclassJpaEntity entity) {
		
		return new Shipclass(
				entity.getShipclassId(),
				entity.getShipclassName(),
				entity.getShipclassDescription()
				);
		
	}
	
	public ShipclassDto fromDomainToDto(Shipclass domain) {
		
		return new ShipclassDto(
				domain.getShipclassId(),
				domain.getShipclassName(),
				domain.getShipclassDescription()
				);
		
	}
	
}
