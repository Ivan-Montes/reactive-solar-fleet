package dev.ime.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import dev.ime.application.dispatcher.QueryDispatcher;
import dev.ime.application.usecase.GetAllPositionQuery;
import dev.ime.application.usecase.GetByIdPositionQuery;
import dev.ime.domain.model.Position;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class QueryService implements QueryServicePort<Position> {

	private final QueryDispatcher queryDispatcher;
	
	public QueryService(QueryDispatcher queryDispatcher) {
		super();
		this.queryDispatcher = queryDispatcher;
	}

	@Override
	public Flux<Position> getAll() {
		
		return handleQuery(new GetAllPositionQuery());
		
	}

	@Override
	public Mono<Position> getById(UUID id) {		

		return handleQuery(new GetByIdPositionQuery(id));
		
	}
	
	private <T> T handleQuery(Query query) {
		
		QueryHandler<T> handler = queryDispatcher.getQueryHandler(query);
		
		return handler.handle(query);
		
	}
	
}
