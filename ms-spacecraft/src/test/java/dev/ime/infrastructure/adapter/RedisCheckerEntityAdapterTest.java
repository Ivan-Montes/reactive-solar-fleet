package dev.ime.infrastructure.adapter;


import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RedisCheckerEntityAdapterTest {

	@Mock
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;  

	@InjectMocks
	private RedisCheckerEntityAdapter redisCheckerEntityAdapter;

    private final UUID shipclassId = UUID.randomUUID();
    private final UUID spacecraftId = UUID.randomUUID();
	
	@Test
	void existsById_WithRightId_ReturnTrue() {
		
		Mockito.when(reactiveRedisTemplate.hasKey(Mockito.anyString())).thenReturn(Mono.just(true));

		Mono<Boolean> result = redisCheckerEntityAdapter.existsById(shipclassId);
		
		StepVerifier.create(result)
		.expectNext(true)
		.verifyComplete();
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void existsAnySpacecrafInCrewMember_WithRightId_ReturnTrue() {

		ReactiveSetOperations<String, Object> reactiveSetOperations = Mockito.mock(ReactiveSetOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForSet()).thenReturn(reactiveSetOperations);
		Mockito.when(reactiveSetOperations.size(Mockito.anyString())).thenReturn(Mono.just(1L));
		
		Mono<Boolean> result = redisCheckerEntityAdapter.existsAnySpacecrafInCrewMember(spacecraftId);
		
		StepVerifier.create(result)
		.expectNext(true)
		.verifyComplete();
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void existsAnySpacecrafInCrewMember_WithRightId_ReturnFalse() {

		ReactiveSetOperations<String, Object> reactiveSetOperations = Mockito.mock(ReactiveSetOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForSet()).thenReturn(reactiveSetOperations);
		Mockito.when(reactiveSetOperations.size(Mockito.anyString())).thenReturn(Mono.just(0L));
		
		Mono<Boolean> result = redisCheckerEntityAdapter.existsAnySpacecrafInCrewMember(spacecraftId);
		
		StepVerifier.create(result)
		.expectNext(false)
		.verifyComplete();
		
	}	

}
