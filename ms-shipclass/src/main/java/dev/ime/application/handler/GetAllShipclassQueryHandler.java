package dev.ime.application.handler;

import org.springframework.stereotype.Component;

import dev.ime.application.usecase.GetAllShipclassQuery;
import dev.ime.domain.model.Shipclass;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;

@Component
public class GetAllShipclassQueryHandler implements QueryHandler<Flux<Shipclass>>{

	private final ReadRepositoryPort<Shipclass> readRepository;

	public GetAllShipclassQueryHandler(ReadRepositoryPort<Shipclass> readRepository) {
		super();
		this.readRepository = readRepository;
	}

	@Override
	public Flux<Shipclass> handle(Query query) {
		
		GetAllShipclassQuery getAllQuery = (GetAllShipclassQuery)query;
		return readRepository.findAll(getAllQuery.pageable());
		
	}
	
}
