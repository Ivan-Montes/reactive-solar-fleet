package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import dev.ime.infrastructure.entity.SpacecraftRedisEntity;
import reactor.core.publisher.Mono;

@Component
public class RedisCheckerEntityAdapter implements RedisCheckerEntityPort {

    private final ReactiveRedisTemplate<String, SpacecraftRedisEntity> reactiveRedisTemplate;
    
	public RedisCheckerEntityAdapter(ReactiveRedisTemplate<String, SpacecraftRedisEntity> reactiveRedisTemplate) {
		super();
		this.reactiveRedisTemplate = reactiveRedisTemplate;		
	}

	public Mono<Boolean> existsAnyByShipclassId(UUID shipclassId) {
		
	    return reactiveRedisTemplate.keys("Spacecraft:*")
	    		.flatMap(key -> reactiveRedisTemplate.opsForValue().get(key)
    	            .onErrorResume( e -> Mono.empty() ))
	            .ofType(SpacecraftRedisEntity.class)
	            .filter(spacecraft -> spacecraft.getShipclassId().equals(shipclassId))
	            .hasElements();
	    
	}
	
}
