package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import dev.ime.config.GlobalConstants;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import dev.ime.infrastructure.entity.CrewMemberRedisEntity;
import reactor.core.publisher.Mono;

@Component
public class RedisCheckerEntityAdapter implements RedisCheckerEntityPort {

    private final ReactiveRedisTemplate<String, CrewMemberRedisEntity> reactiveRedisTemplate;
    
	public RedisCheckerEntityAdapter(
			ReactiveRedisTemplate<String, CrewMemberRedisEntity> reactiveRedisTemplate) {
		super();
		this.reactiveRedisTemplate = reactiveRedisTemplate;
	}

	@Override
	public Mono<Boolean> existsAnyPositionInCrewMember(UUID positionId) {
		
	    return reactiveRedisTemplate.keys(GlobalConstants.CREWMEMBER_CAT + ":*")
	    		.flatMap(key -> reactiveRedisTemplate.opsForValue().get(key)
    	            .onErrorResume( e -> Mono.empty() ))
	            .ofType(CrewMemberRedisEntity.class)
	            .filter(entity -> entity.getPositionId().equals(positionId))
	            .hasElements();
	    
	}

}
