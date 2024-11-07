package dev.ime.infrastructure.adapter;


import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RedisCheckerEntityAdapterTest {

	@Mock
    private ReactiveRedisTemplate<String, ?> reactiveRedisTemplate;

	@InjectMocks
	private RedisCheckerEntityAdapter redisCheckerEntityAdapter;

    private final UUID spacecraftId = UUID.randomUUID();
    private final UUID positionId = UUID.randomUUID();
	
	
	@Test
	void existsPosition_WithRighId_ReturnTrue() {
		
		Mockito.when(reactiveRedisTemplate.hasKey(Mockito.anyString())).thenReturn(Mono.just(true));
		
		Mono<Boolean> result = redisCheckerEntityAdapter.existsPosition(positionId);
		
		StepVerifier
		.create(result)
		.expectNext(true)
		.verifyComplete();
		Mockito.verify(reactiveRedisTemplate).hasKey(Mockito.anyString());
		
	}

	@Test
	void existsSpacecraft_WithRighId_ReturnTrue() {
		
		Mockito.when(reactiveRedisTemplate.hasKey(Mockito.anyString())).thenReturn(Mono.just(true));
		
		Mono<Boolean> result = redisCheckerEntityAdapter.existsSpacecraft(spacecraftId);
		
		StepVerifier
		.create(result)
		.expectNext(true)
		.verifyComplete();
		Mockito.verify(reactiveRedisTemplate).hasKey(Mockito.anyString());
		
	}
	
}
