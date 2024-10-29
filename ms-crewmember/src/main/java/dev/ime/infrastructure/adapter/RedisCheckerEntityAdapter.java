package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import dev.ime.config.GlobalConstants;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import dev.ime.infrastructure.entity.PositionRedisEntity;
import dev.ime.infrastructure.entity.SpacecraftRedisEntity;
import reactor.core.publisher.Mono;

@Component
public class RedisCheckerEntityAdapter implements RedisCheckerEntityPort{

    private final ReactiveRedisTemplate<String, PositionRedisEntity> positionReactiveRedisTemplate;
    private final ReactiveRedisTemplate<String, SpacecraftRedisEntity> spacecraftReactiveRedisTemplate;       
    
	public RedisCheckerEntityAdapter(ReactiveRedisTemplate<String, PositionRedisEntity> positionReactiveRedisTemplate,
			ReactiveRedisTemplate<String, SpacecraftRedisEntity> spacecraftReactiveRedisTemplate) {
		super();
		this.positionReactiveRedisTemplate = positionReactiveRedisTemplate;
		this.spacecraftReactiveRedisTemplate = spacecraftReactiveRedisTemplate;
	}

	@Override
	public Mono<Boolean> existsPosition(UUID positionId) {
		
		return positionReactiveRedisTemplate.hasKey( GlobalConstants.POSITION_CAT + ":" +  positionId.toString());
		
	}

	@Override
	public Mono<Boolean> existsSpacecraft(UUID spacecraftId) {
		
		return spacecraftReactiveRedisTemplate.hasKey( GlobalConstants.SPACECRAFT_CAT + ":" +  spacecraftId.toString());

	}

}
