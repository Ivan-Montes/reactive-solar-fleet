package dev.ime.config;

import org.springframework.stereotype.Component;

import dev.ime.application.dto.PositionDto;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Position;
import dev.ime.infrastructure.entity.EventMongoEntity;
import dev.ime.infrastructure.entity.PositionJpaEntity;

@Component
public class PositionMapper {

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
	
	public Position fromJpaToDomain(PositionJpaEntity entity) {
		
		return new Position(
				entity.getPositionId(),
				entity.getPositionName(),
				entity.getPositionDescription()
				);
		
	}
	
	public PositionDto fromDomainToDto(Position domain) {
		
		return new PositionDto(
				domain.getPositionId(),
				domain.getPositionName(),
				domain.getPositionDescription()
				);
		
	}
	
}
