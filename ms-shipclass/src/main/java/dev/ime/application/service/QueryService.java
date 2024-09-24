package dev.ime.application.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dispatcher.QueryDispatcher;
import dev.ime.application.dto.ShipclassDto;
import dev.ime.application.usecase.GetAllShipclassQuery;
import dev.ime.application.usecase.GetByIdShipclassQuery;
import dev.ime.config.ShipclassMapper;
import dev.ime.domain.model.Shipclass;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class QueryService implements QueryServicePort<ShipclassDto> {

	private final QueryDispatcher queryDispatcher;
	private final ShipclassMapper mapper;	
	
	public QueryService(QueryDispatcher queryDispatcher, ShipclassMapper mapper) {
		super();
		this.queryDispatcher = queryDispatcher;
		this.mapper = mapper;
	}

	@Override
	public Flux<ShipclassDto> getAll(Pageable pageable) {

		GetAllShipclassQuery query = new GetAllShipclassQuery(pageable);
		QueryHandler<Flux<Shipclass>> handler = queryDispatcher.getQueryHandler(query);		
		Flux<Shipclass> flow = handler.handle(query);
		
		return flow.map(mapper::fromDomainToDto);
		
	}

	@Override
	public Mono<ShipclassDto> getById(UUID id) {		

		GetByIdShipclassQuery query = new GetByIdShipclassQuery(id);
		QueryHandler<Mono<Shipclass>> handler = queryDispatcher.getQueryHandler(query);		
		Mono<Shipclass> flow = handler.handle(query);
		
		return flow.map(mapper::fromDomainToDto);
		
	}
	
}
