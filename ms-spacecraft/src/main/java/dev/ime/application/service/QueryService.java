package dev.ime.application.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dispatcher.QueryDispatcher;
import dev.ime.application.dto.SpacecraftDto;
import dev.ime.application.usecase.GetAllSpacecraftQuery;
import dev.ime.application.usecase.GetByIdSpacecraftQuery;
import dev.ime.config.SpacecraftMapper;
import dev.ime.domain.model.Spacecraft;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class QueryService implements QueryServicePort<SpacecraftDto> {

	private final QueryDispatcher queryDispatcher;
	private final SpacecraftMapper mapper;
	
	public QueryService(QueryDispatcher queryDispatcher, SpacecraftMapper mapper) {
		super();
		this.queryDispatcher = queryDispatcher;
		this.mapper = mapper;
	}

	@Override
	public Flux<SpacecraftDto> getAll(Pageable pageable) {
		
		GetAllSpacecraftQuery query = new GetAllSpacecraftQuery(pageable);
		QueryHandler<Flux<Spacecraft>> handler = queryDispatcher.getQueryHandler(query);		
		Flux<Spacecraft> flow = handler.handle(query);
		
		return flow.map(mapper::fromDomainToDto);
		
	}

	@Override
	public Mono<SpacecraftDto> getById(UUID id) {		

		GetByIdSpacecraftQuery query = new GetByIdSpacecraftQuery(id);
		QueryHandler<Mono<Spacecraft>> handler = queryDispatcher.getQueryHandler(query);		
		Mono<Spacecraft> flow = handler.handle(query);
		
		return flow.map(mapper::fromDomainToDto);
		
	}
	
}
