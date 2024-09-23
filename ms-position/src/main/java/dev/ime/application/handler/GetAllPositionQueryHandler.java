package dev.ime.application.handler;

import org.springframework.stereotype.Component;

import dev.ime.application.usecase.GetAllPositionQuery;
import dev.ime.domain.model.Position;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;

@Component
public class GetAllPositionQueryHandler implements QueryHandler<Flux<Position>>{

	private final ReadRepositoryPort<Position> readRepository;
	
	public GetAllPositionQueryHandler(ReadRepositoryPort<Position> readRepository) {
		super();
		this.readRepository = readRepository;
	}

	@Override
	public Flux<Position> handle(Query query) {
		
		GetAllPositionQuery getAllQuery = (GetAllPositionQuery)query;
		
		return readRepository.findAll(getAllQuery.pageable());
		
	}

}
