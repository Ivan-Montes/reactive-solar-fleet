package dev.ime.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import dev.ime.infrastructure.entity.PositionJpaEntity;
import reactor.core.publisher.Mono;

public interface PositionReadRepository extends ReactiveCrudRepository<PositionJpaEntity, UUID>{

	Mono<PositionJpaEntity> findByPositionName(String name);
	
}
