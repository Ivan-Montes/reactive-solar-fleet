package dev.ime.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import dev.ime.application.dispatcher.QueryDispatcher;
import dev.ime.application.usecase.GetAllShipclassQuery;
import dev.ime.application.usecase.GetByIdShipclassQuery;
import dev.ime.domain.model.Shipclass;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class QueryService implements QueryServicePort<Shipclass> {

	private final QueryDispatcher queryDispatcher;
	
	public QueryService(QueryDispatcher queryDispatcher) {
		super();
		this.queryDispatcher = queryDispatcher;
	}

	@Override
	public Flux<Shipclass> getAll() {
		
		return handleQuery(new GetAllShipclassQuery());
		
	}

	@Override
	public Mono<Shipclass> getById(UUID id) {		

		return handleQuery(new GetByIdShipclassQuery(id));
		
	}
	
	private <T> T handleQuery(Query query) {
		
		QueryHandler<T> handler = queryDispatcher.getQueryHandler(query);
		
		return handler.handle(query);
		
	}
	
}
