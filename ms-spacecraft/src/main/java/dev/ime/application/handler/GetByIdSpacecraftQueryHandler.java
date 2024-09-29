package dev.ime.application.handler;

import org.springframework.stereotype.Component;

import dev.ime.application.usecase.GetByIdSpacecraftQuery;
import dev.ime.domain.model.Spacecraft;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetByIdSpacecraftQueryHandler implements QueryHandler<Mono<Spacecraft>>{
	
	private final ReadRepositoryPort<Spacecraft> readRepository;

	public GetByIdSpacecraftQueryHandler(ReadRepositoryPort<Spacecraft> readRepository) {
		super();
		this.readRepository = readRepository;
	}

	@Override
	public Mono<Spacecraft> handle(Query query) {
		
		 GetByIdSpacecraftQuery getByIdQuery = (GetByIdSpacecraftQuery) query;
			
			return readRepository
					.findById(getByIdQuery.spacecraftId());
			
	}

}
