package dev.ime.domain.port.outbound;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface RedisCheckerEntityPort {

	Mono<Boolean> existsAnyPositionInCrewMember(UUID positionId);
	
}