package dev.ime.application.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dispatcher.QueryDispatcher;
import dev.ime.application.dto.PositionDto;
import dev.ime.application.usecase.GetAllPositionQuery;
import dev.ime.application.usecase.GetByIdPositionQuery;
import dev.ime.config.PositionMapper;
import dev.ime.domain.model.Position;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class QueryService implements QueryServicePort<PositionDto> {

	private final QueryDispatcher queryDispatcher;
	private final PositionMapper mapper;	

	public QueryService(QueryDispatcher queryDispatcher, PositionMapper mapper) {
		super();
		this.queryDispatcher = queryDispatcher;
		this.mapper = mapper;
	}

	@Override
	public Flux<PositionDto> getAll(Pageable pageable) {
		
		GetAllPositionQuery query = new GetAllPositionQuery(pageable);
		QueryHandler<Flux<Position>> handler = queryDispatcher.getQueryHandler(query);		
		Flux<Position> flow = handler.handle(query);
		
		return flow.map(mapper::fromDomainToDto);
		
	}

	@Override
	public Mono<PositionDto> getById(UUID id) {		
		
		GetByIdPositionQuery query = new GetByIdPositionQuery(id);
		QueryHandler<Mono<Position>> handler = queryDispatcher.getQueryHandler(query);		
		Mono<Position> flow = handler.handle(query);
		
		return flow.map(mapper::fromDomainToDto);
		
	}
	
}
