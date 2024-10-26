package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import dev.ime.config.GlobalConstants;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import dev.ime.infrastructure.entity.ShipclassRedisEntity;
import reactor.core.publisher.Mono;

@Component
public class RedisCheckerEntityAdapter implements RedisCheckerEntityPort {

    private final ReactiveRedisTemplate<String, ShipclassRedisEntity> shipclassReactiveRedisTemplate;
    private final ReactiveRedisTemplate<String, String> stringReactiveRedisTemplate;

	public RedisCheckerEntityAdapter(ReactiveRedisTemplate<String, ShipclassRedisEntity> shipclassReactiveRedisTemplate,
			ReactiveRedisTemplate<String, String> stringReactiveRedisTemplate) {
		super();
		this.shipclassReactiveRedisTemplate = shipclassReactiveRedisTemplate;
		this.stringReactiveRedisTemplate = stringReactiveRedisTemplate;
	}
	
	@Override
	public Mono<Boolean> existsById(UUID id) {
		
        return shipclassReactiveRedisTemplate.hasKey( GlobalConstants.SHIPCLASS_CAT  + ":" + id.toString() );
		
	}	

	@Override
	public Mono<Boolean> existsAnySpacecrafInCrewMember(UUID spacecraftId) {
		
	    String indexKey = GlobalConstants.SPACECRAFT_CAT_INDEX + spacecraftId;
	    
	    return stringReactiveRedisTemplate.opsForSet().size(indexKey)
	        .map(size -> size > 0);
	    
	}

}
