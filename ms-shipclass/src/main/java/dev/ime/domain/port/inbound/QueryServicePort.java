package dev.ime.domain.port.inbound;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface QueryServicePort<T> {

	Flux<T>getAll();
	Mono<T>getById(UUID id);
	
}
