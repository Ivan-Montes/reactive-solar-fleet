package dev.ime.application.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecase.GetByIdCrewMemberQuery;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.model.CrewMember;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetByIdCrewMemberQueryHandler implements QueryHandler<Mono<CrewMember>> {

	private final ReadRepositoryPort<CrewMember> readRepository;

	public GetByIdCrewMemberQueryHandler(ReadRepositoryPort<CrewMember> readRepository) {
		super();
		this.readRepository = readRepository;
	}

	@Override
	public Mono<CrewMember> handle(Query query) {
		
		return Mono.justOrEmpty(query)
				.cast(GetByIdCrewMemberQuery.class)
				.map(GetByIdCrewMemberQuery::crewMemberId)
				.flatMap(readRepository::findById)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.CREWMEMBER_ID, ((GetByIdCrewMemberQuery)query).crewMemberId().toString()))));
		
	}
	
}
