package dev.ime.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import dev.ime.infrastructure.entity.PositionJpaEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PositionReadRepository extends ReactiveCrudRepository<PositionJpaEntity, UUID>, ReactiveSortingRepository<PositionJpaEntity, UUID>{

	Mono<PositionJpaEntity> findByPositionName(String name);
    Flux<PositionJpaEntity> findAllBy(Pageable pageable);

}
