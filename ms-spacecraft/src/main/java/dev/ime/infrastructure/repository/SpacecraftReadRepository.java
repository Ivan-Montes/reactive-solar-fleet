package dev.ime.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import dev.ime.infrastructure.entity.SpacecraftJpaEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpacecraftReadRepository extends ReactiveCrudRepository<SpacecraftJpaEntity, UUID>, ReactiveSortingRepository<SpacecraftJpaEntity, UUID>{

	Mono<SpacecraftJpaEntity> findBySpacecraftName(String name);
	Flux<SpacecraftJpaEntity> findAllBy(Pageable pageable);
	
}
