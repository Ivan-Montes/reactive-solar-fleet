package dev.ime.application.handler;

import org.springframework.stereotype.Component;

import dev.ime.application.usecase.GetAllSpacecraftQuery;
import dev.ime.domain.model.Spacecraft;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;

@Component
public class GetAllSpacecraftQueryHandler  implements QueryHandler<Flux<Spacecraft>>{
	
	private final ReadRepositoryPort<Spacecraft> readRepository;
	
	public GetAllSpacecraftQueryHandler(ReadRepositoryPort<Spacecraft> readRepository) {
		super();
		this.readRepository = readRepository;
	}

	@Override
	public Flux<Spacecraft> handle(Query query) {
		
		GetAllSpacecraftQuery getAllQuery = (GetAllSpacecraftQuery)query;		
		return readRepository.findAll(getAllQuery.pageable());
		
	}
	
}
