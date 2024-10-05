package dev.ime.config;

import org.springframework.stereotype.Component;

import dev.ime.application.dto.CrewMemberDto;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.CrewMember;
import dev.ime.infrastructure.entity.EventMongoEntity;
import dev.ime.infrastructure.entity.CrewMemberJpaEntity;

@Component
public class CrewMemberMapper {

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
	
	public CrewMember fromJpaToDomain(CrewMemberJpaEntity entity) {
		
		return new CrewMember(
				entity.getCrewMemberId(),
				entity.getCrewMemberName(),
				entity.getCrewMemberSurname(),
				entity.getPositionId(),
				entity.getSpacecraftId()
				);
		
	}
	
	public CrewMemberDto fromDomainToDto(CrewMember domain) {
		
		return new CrewMemberDto(
				domain.getCrewMemberId(),
				domain.getCrewMemberName(),
				domain.getCrewMemberSurname(),
				domain.getPositionId(),
				domain.getSpacecraftId()
				);
		
	}
	
}
