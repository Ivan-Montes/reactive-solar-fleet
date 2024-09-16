package dev.ime.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import dev.ime.infrastructure.entity.ShipclassJpaEntity;
import reactor.core.publisher.Mono;

public interface ShipclassReadRepository extends ReactiveCrudRepository<ShipclassJpaEntity, UUID>{

	Mono<ShipclassJpaEntity> findByShipclassName(String name);
	
}