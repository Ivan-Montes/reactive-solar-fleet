package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.config.CrewMemberMapper;
import dev.ime.domain.model.CrewMember;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.infrastructure.repository.CrewMemberReadRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class CrewMemberReadRepositoryAdapter implements ReadRepositoryPort<CrewMember>{

	private final CrewMemberReadRepository crewMemberRepository;
	private final CrewMemberMapper mapper;
	
	public CrewMemberReadRepositoryAdapter(CrewMemberReadRepository crewMemberRepository, CrewMemberMapper mapper) {
		super();
		this.crewMemberRepository = crewMemberRepository;
		this.mapper = mapper;
	}

	@Override
	public Flux<CrewMember> findAll(Pageable pageable) {
		
		return crewMemberRepository
				.findAllBy(pageable)
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public Mono<CrewMember> findById(UUID id) {

		return crewMemberRepository
				.findById(id)
				.map(mapper::fromJpaToDomain);				
				
	}

	@Override
	public Flux<CrewMember> findByNameAndSurname(String name, String surname) {

		return crewMemberRepository
				.findByCrewMemberNameAndCrewMemberSurname(name, surname)
				.map(mapper::fromJpaToDomain);
	}

}
