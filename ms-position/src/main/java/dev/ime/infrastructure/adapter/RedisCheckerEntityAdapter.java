package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import dev.ime.config.GlobalConstants;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import reactor.core.publisher.Mono;

@Component
public class RedisCheckerEntityAdapter implements RedisCheckerEntityPort {

    private final ReactiveRedisTemplate<String, String> stringReactiveRedisTemplate;

	public RedisCheckerEntityAdapter(ReactiveRedisTemplate<String, String> stringReactiveRedisTemplate) {
		super();
		this.stringReactiveRedisTemplate = stringReactiveRedisTemplate;
	}

	@Override
	public Mono<Boolean> existsAnyPositionInCrewMember(UUID positionId) {
		
		String indexKey = GlobalConstants.POSITION_CAT_INDEX + positionId;
	    
	    return stringReactiveRedisTemplate.opsForSet().size(indexKey)
	        .map(size -> size > 0);
	    
	}

}
