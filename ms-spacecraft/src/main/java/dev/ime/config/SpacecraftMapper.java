package dev.ime.config;

import org.springframework.stereotype.Component;

import dev.ime.application.dto.SpacecraftDto;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Spacecraft;
import dev.ime.infrastructure.entity.EventMongoEntity;
import dev.ime.infrastructure.entity.SpacecraftJpaEntity;

@Component
public class SpacecraftMapper {

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
	
	public Spacecraft fromJpaToDomain(SpacecraftJpaEntity entity) {
		
		return new Spacecraft(
				entity.getSpacecraftId(),
				entity.getSpacecraftName(),
				entity.getShipclassId()
				);
		
	}
	
	public SpacecraftDto fromDomainToDto(Spacecraft domain) {
		
		return new SpacecraftDto(
				domain.getSpacecraftId(),
				domain.getSpacecraftName(),
				domain.getShipclassId()
				);
		
	}
	
}
