package dev.ime.domain.port.inbound;


import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

public interface CommandEndpointPort {

	Mono<ServerResponse>create(ServerRequest serverRequest);
	Mono<ServerResponse>update(ServerRequest serverRequest);
	Mono<ServerResponse>deleteById(ServerRequest serverRequest);
	
}
