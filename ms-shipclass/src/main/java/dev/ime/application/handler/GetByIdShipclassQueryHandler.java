package dev.ime.application.handler;

import org.springframework.stereotype.Component;

import dev.ime.application.usecase.GetByIdShipclassQuery;
import dev.ime.domain.model.Shipclass;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetByIdShipclassQueryHandler implements QueryHandler<Mono<Shipclass>>{

	private final ReadRepositoryPort<Shipclass> readRepository;

	public GetByIdShipclassQueryHandler(ReadRepositoryPort<Shipclass> readRepository) {
		super();
		this.readRepository = readRepository;
	}

	@Override
	public Mono<Shipclass> handle(Query query) {
		
		 GetByIdShipclassQuery getByIdQuery = (GetByIdShipclassQuery) query;
		
		return readRepository
				.findById(getByIdQuery.shipclassId());
		
	}
	
}
