package dev.ime.application.handler;

import org.springframework.stereotype.Component;

import dev.ime.application.usecase.GetAllCrewMemberQuery;
import dev.ime.domain.model.CrewMember;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;

@Component
public class GetAllCrewMemberQueryHandler implements QueryHandler<Flux<CrewMember>>{

	private final ReadRepositoryPort<CrewMember> readRepository;
	
	public GetAllCrewMemberQueryHandler(ReadRepositoryPort<CrewMember> readRepository) {
		super();
		this.readRepository = readRepository;
	}

	@Override
	public Flux<CrewMember> handle(Query query) {
		
		GetAllCrewMemberQuery getAllQuery =  (GetAllCrewMemberQuery)query;

		return readRepository.findAll(getAllQuery.pageable());
		
	}

}
