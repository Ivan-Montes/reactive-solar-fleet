package dev.ime.domain.port.outbound;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface RedisCheckerEntityPort {

	Mono<Boolean> existsById(UUID id);
	Mono<Boolean> existsAnySpacecrafInCrewMember(UUID positionId);
	
	
}
