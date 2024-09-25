package dev.ime.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import dev.ime.infrastructure.entity.ShipclassJpaEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ShipclassReadRepository extends ReactiveCrudRepository<ShipclassJpaEntity, UUID>, ReactiveSortingRepository<ShipclassJpaEntity, UUID>{

	Mono<ShipclassJpaEntity> findByShipclassName(String name);
	Flux<ShipclassJpaEntity> findAllBy(Pageable pageable);
	
}