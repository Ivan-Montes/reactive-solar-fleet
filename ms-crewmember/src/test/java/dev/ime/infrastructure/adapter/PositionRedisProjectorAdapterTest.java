package dev.ime.infrastructure.adapter;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;

import dev.ime.application.exception.CreateRedisEntityException;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.event.Event;
import dev.ime.infrastructure.entity.PositionRedisEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PositionRedisProjectorAdapterTest {

	@Mock
	private LoggerUtil loggerUtil;
	
	@Mock
    private ReactiveRedisTemplate<String, PositionRedisEntity> reactiveRedisTemplate;

	@InjectMocks
    private PositionRedisProjectorAdapter positionRedisProjectorAdapter;

	private Event event;
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.POSITION_CAT;
	private final String eventType = GlobalConstants.POSITION_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();

    private final UUID positionId = UUID.randomUUID();
    
	@BeforeEach
	private void setUp() {

		eventData.put(GlobalConstants.POSITION_ID, positionId.toString());
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
	}	

	@SuppressWarnings("unchecked")
	@Test
	void create_WithEvent_SaveValue() {
		
		ReactiveValueOperations<String, PositionRedisEntity> reactiveValueOperations = Mockito.mock(ReactiveValueOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
		Mockito.when(reactiveValueOperations.set(Mockito.anyString(), Mockito.any(PositionRedisEntity.class))).thenReturn(Mono.just(true));
		
		StepVerifier
		.create(positionRedisProjectorAdapter.create(event))
		.verifyComplete();
		
		Mockito.verify(reactiveRedisTemplate).opsForValue();
		Mockito.verify(reactiveValueOperations).set(Mockito.anyString(), Mockito.any(PositionRedisEntity.class));
		
	}

	@Test
	void create_WithEmptyEventData_ReturnMonoErrorOfCreateRedisException() {
		
		eventData.clear();
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
		StepVerifier
		.create(positionRedisProjectorAdapter.create(event))
		.expectError(CreateRedisEntityException.class)
		.verify();
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void deleteById_WithEvent_DeleteValue() {
		
		ReactiveValueOperations<String, PositionRedisEntity> reactiveValueOperations = Mockito.mock(ReactiveValueOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
		Mockito.when(reactiveValueOperations.delete(Mockito.anyString())).thenReturn(Mono.just(true));
		
		StepVerifier
		.create(positionRedisProjectorAdapter.deleteById(event))
		.verifyComplete();
		
		Mockito.verify(reactiveRedisTemplate).opsForValue();
		Mockito.verify(reactiveValueOperations).delete(Mockito.anyString());
		
	}
	
}
