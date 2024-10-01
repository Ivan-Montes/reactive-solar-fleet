package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import dev.ime.config.GlobalConstants;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import dev.ime.infrastructure.entity.CrewMemberRedisEntity;
import dev.ime.infrastructure.entity.ShipclassRedisEntity;
import reactor.core.publisher.Mono;

@Component
public class RedisCheckerEntityAdapter implements RedisCheckerEntityPort {

    private final ReactiveRedisTemplate<String, ShipclassRedisEntity> shipclassReactiveRedisTemplate;
    private final ReactiveRedisTemplate<String, CrewMemberRedisEntity> crewMemberReactiveRedisTemplate;    

	public RedisCheckerEntityAdapter(
			ReactiveRedisTemplate<String, ShipclassRedisEntity> shipclassReactiveRedisTemplate,
			ReactiveRedisTemplate<String, CrewMemberRedisEntity> crewMemberReactiveRedisTemplate) {
		super();
		this.shipclassReactiveRedisTemplate = shipclassReactiveRedisTemplate;
		this.crewMemberReactiveRedisTemplate = crewMemberReactiveRedisTemplate;
	}

	@Override
	public Mono<Boolean> existsById(UUID id) {
		
        return shipclassReactiveRedisTemplate.hasKey( GlobalConstants.SHIPCLASS_CAT  + ":" + id.toString() );
		
	}	

	@Override
	public Mono<Boolean> existsAnySpacecrafInCrewMember(UUID spacecraftId) {
		
	    return crewMemberReactiveRedisTemplate.keys(GlobalConstants.CREWMEMBER_CAT + ":*")
	    		.flatMap(key -> crewMemberReactiveRedisTemplate.opsForValue().get(key)
    	            .onErrorResume( e -> Mono.empty() ))
	            .ofType(CrewMemberRedisEntity.class)
	            .filter(entity -> entity.getSpacecraftId().equals(spacecraftId))
	            .hasElements();
	    
	}
	
}
