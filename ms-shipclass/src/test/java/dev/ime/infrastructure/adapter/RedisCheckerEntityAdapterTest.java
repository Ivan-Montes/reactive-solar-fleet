package dev.ime.infrastructure.adapter;


import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;

import dev.ime.infrastructure.entity.SpacecraftRedisEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RedisCheckerEntityAdapterTest {

	@Mock
    private ReactiveRedisTemplate<String, SpacecraftRedisEntity> reactiveRedisTemplate;

	@InjectMocks
	private RedisCheckerEntityAdapter spacecraftRedisDbCheckerEntityAdapter;

    private final UUID spacecraftId = UUID.randomUUID();
    private final UUID shipclassId = UUID.randomUUID();
    

	@SuppressWarnings("unchecked")
	@Test
	void existsAny_WithRighId_ReturnTrue() {
		
		Mockito.when(reactiveRedisTemplate.keys(Mockito.anyString())).thenReturn(Flux.just(spacecraftId.toString()));
		ReactiveValueOperations<String, SpacecraftRedisEntity> reactiveValueOperations = Mockito.mock(ReactiveValueOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
		Mockito.when(reactiveValueOperations.get(Mockito.anyString())).thenReturn(Mono.just(new SpacecraftRedisEntity(spacecraftId, shipclassId)));
		
		Mono<Boolean> result = spacecraftRedisDbCheckerEntityAdapter.existsAnyByShipclassId(shipclassId);
		
		StepVerifier.create(result)
		.expectNext(true)
		.verifyComplete();
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void existsAny_WithCollisionId_ReturnFalse() {
		
		Mockito.when(reactiveRedisTemplate.keys(Mockito.anyString())).thenReturn(Flux.just(spacecraftId.toString()));
		ReactiveValueOperations<String, SpacecraftRedisEntity> reactiveValueOperations = Mockito.mock(ReactiveValueOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
		Mockito.when(reactiveValueOperations.get(Mockito.anyString())).thenReturn(Mono.error(new RuntimeException()));
		
		Mono<Boolean> result = spacecraftRedisDbCheckerEntityAdapter.existsAnyByShipclassId(shipclassId);
		
		StepVerifier.create(result)
		.expectNext(false)
		.verifyComplete();
		
	}

}
