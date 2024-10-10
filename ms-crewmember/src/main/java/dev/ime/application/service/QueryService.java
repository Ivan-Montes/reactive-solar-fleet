package dev.ime.application.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dispatcher.QueryDispatcher;
import dev.ime.application.dto.CrewMemberDto;
import dev.ime.application.usecase.GetAllCrewMemberQuery;
import dev.ime.application.usecase.GetByIdCrewMemberQuery;
import dev.ime.config.CrewMemberMapper;
import dev.ime.domain.model.CrewMember;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class QueryService implements QueryServicePort<CrewMemberDto>{

	private final QueryDispatcher queryDispatcher;
	private final CrewMemberMapper mapper;	
	
	public QueryService(QueryDispatcher queryDispatcher, CrewMemberMapper mapper) {
		super();
		this.queryDispatcher = queryDispatcher;
		this.mapper = mapper;
	}

	@Override
	public Flux<CrewMemberDto> getAll(Pageable pageable) {
		
		GetAllCrewMemberQuery query = new GetAllCrewMemberQuery(pageable);
		QueryHandler<Flux<CrewMember>> handler = queryDispatcher.getQueryHandler(query);		
		Flux<CrewMember> flow = handler.handle(query);
		
		return flow.map(mapper::fromDomainToDto);
		
	}

	@Override
	public Mono<CrewMemberDto> getById(UUID id) {
		
		GetByIdCrewMemberQuery query = new GetByIdCrewMemberQuery(id);
		QueryHandler<Mono<CrewMember>> handler = queryDispatcher.getQueryHandler(query);		
		Mono<CrewMember> flow = handler.handle(query);
		
		return flow.map(mapper::fromDomainToDto);
		
	}

}
