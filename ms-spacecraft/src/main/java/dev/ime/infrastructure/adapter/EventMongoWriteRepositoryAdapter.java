package dev.ime.infrastructure.adapter;

import org.springframework.stereotype.Repository;

import dev.ime.config.SpacecraftMapper;
import dev.ime.domain.event.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.infrastructure.entity.EventMongoEntity;
import dev.ime.infrastructure.repository.EventMongoWriteRepository;
import reactor.core.publisher.Mono;

@Repository
public class EventMongoWriteRepositoryAdapter implements EventWriteRepositoryPort{

	private final EventMongoWriteRepository eventMongoWriteRepository;
	private final SpacecraftMapper mapper;
	
	public EventMongoWriteRepositoryAdapter(EventMongoWriteRepository eventMongoWriteRepository,
			SpacecraftMapper mapper) {
		super();
		this.eventMongoWriteRepository = eventMongoWriteRepository;
		this.mapper = mapper;
	}

	@Override
	public Mono<Event> save(Event event) {
		
		EventMongoEntity eventMongoEntity = mapper.fromEventDomainToEventMongo(event);
		
		return eventMongoWriteRepository.save(eventMongoEntity)
				.map(mapper::fromEventMongoToEventDomain);
		
	}

}
