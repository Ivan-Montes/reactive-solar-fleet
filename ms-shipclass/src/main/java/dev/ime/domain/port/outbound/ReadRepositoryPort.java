package dev.ime.domain.port.outbound;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReadRepositoryPort<T> {

	Flux<T> findAll();
	Mono<T> findById(UUID id);
	Mono<T> findByName(String name);
	
}
