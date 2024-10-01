package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.config.SpacecraftMapper;
import dev.ime.domain.model.Spacecraft;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.infrastructure.repository.SpacecraftReadRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class SpacecraftReadRepositoryAdapter implements ReadRepositoryPort<Spacecraft>{

	private final SpacecraftReadRepository spacecraftReadRepository;
	private final SpacecraftMapper mapper;
	
	public SpacecraftReadRepositoryAdapter(SpacecraftReadRepository spacecraftReadRepository, SpacecraftMapper mapper) {
		super();
		this.spacecraftReadRepository = spacecraftReadRepository;
		this.mapper = mapper;
	}

	@Override
	public Flux<Spacecraft> findAll(Pageable pageable) {
		return spacecraftReadRepository
				.findAllBy(pageable)
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public Mono<Spacecraft> findById(UUID id) {
		return spacecraftReadRepository
				.findById(id)
				.map(mapper::fromJpaToDomain);	
	}

	@Override
	public Mono<Spacecraft> findByName(String name) {
		return spacecraftReadRepository
				.findBySpacecraftName(name)
				.map(mapper::fromJpaToDomain);
	}

}
