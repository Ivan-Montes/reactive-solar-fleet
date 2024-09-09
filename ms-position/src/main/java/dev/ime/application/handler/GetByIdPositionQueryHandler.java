package dev.ime.application.handler;

import org.springframework.stereotype.Component;

import dev.ime.application.usecase.GetByIdPositionQuery;
import dev.ime.domain.model.Position;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetByIdPositionQueryHandler implements QueryHandler<Mono<Position>>{

	private final ReadRepositoryPort<Position> readRepository;
	
	public GetByIdPositionQueryHandler(ReadRepositoryPort<Position> readRepository) {
		super();
		this.readRepository = readRepository;
	}

	@Override
	public Mono<Position> handle(Query query) {
		
		GetByIdPositionQuery getByIdQuery = (GetByIdPositionQuery) query;
		
		return readRepository
				.findById(getByIdQuery.positionId());
		
	}

	
}
