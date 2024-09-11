package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import dev.ime.config.PositionMapper;
import dev.ime.domain.model.Position;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.infrastructure.repository.PositionReadRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class PositionReadRepositoryAdapter implements ReadRepositoryPort<Position>{

	private final PositionReadRepository positionReadRepository;
	private final PositionMapper mapper;
	
	public PositionReadRepositoryAdapter(PositionReadRepository positionReadRepository, PositionMapper mapper) {
		super();
		this.positionReadRepository = positionReadRepository;
		this.mapper = mapper;
	}

	@Override
	public Flux<Position> findAll() {
		
		return positionReadRepository
		.findAll()
		.map(mapper::fromJpaToDomain);
		
	}

	@Override
	public Mono<Position> findById(UUID id) {
		
		return positionReadRepository
				.findById(id)
				.map(mapper::fromJpaToDomain);				
				
	}

	@Override
	public Mono<Position> findByName(String name) {
		
		return positionReadRepository
				.findByPositionName(name)
				.map(mapper::fromJpaToDomain);
	}

}
