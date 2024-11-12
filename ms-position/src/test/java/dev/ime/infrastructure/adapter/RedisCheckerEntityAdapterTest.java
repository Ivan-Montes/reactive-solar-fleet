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
	
    private final UUID positionId = UUID.randomUUID();
    
    @SuppressWarnings("unchecked")
	@Test
	void existsAnyPositionInCrewMember_WithRightId_ReturnTrue() {

		ReactiveSetOperations<String, Object> reactiveSetOperations = Mockito.mock(ReactiveSetOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForSet()).thenReturn(reactiveSetOperations);
		Mockito.when(reactiveSetOperations.size(Mockito.anyString())).thenReturn(Mono.just(1L));
		
		Mono<Boolean> result = redisCheckerEntityAdapter.existsAnyPositionInCrewMember(positionId);
		
		StepVerifier.create(result)
		.expectNext(true)
		.verifyComplete();
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void existsAnyPositionInCrewMember_WithRightId_ReturnFalse() {

		ReactiveSetOperations<String, Object> reactiveSetOperations = Mockito.mock(ReactiveSetOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForSet()).thenReturn(reactiveSetOperations);
		Mockito.when(reactiveSetOperations.size(Mockito.anyString())).thenReturn(Mono.just(0L));
		
		Mono<Boolean> result = redisCheckerEntityAdapter.existsAnyPositionInCrewMember(positionId);
		
		StepVerifier.create(result)
		.expectNext(false)
		.verifyComplete();
		
	}

}
