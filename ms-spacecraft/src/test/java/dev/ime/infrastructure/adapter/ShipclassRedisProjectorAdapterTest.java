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
import dev.ime.infrastructure.entity.ShipclassRedisEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ShipclassRedisProjectorAdapterTest {

	@Mock
	private LoggerUtil loggerUtil;
	
	@Mock
    private ReactiveRedisTemplate<String, ShipclassRedisEntity> reactiveRedisTemplate;

	@InjectMocks
    private ShipclassRedisProjectorAdapter shipclassRedisProjectorAdapter;

	private Event event;
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.SHIPCLASS_ID;
	private final String eventType = GlobalConstants.SHIPCLASS_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();

	private final UUID shipclassId = UUID.randomUUID();
    
	@BeforeEach
	private void setUp() {

		eventData.put(GlobalConstants.SHIPCLASS_ID, shipclassId.toString());
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
	}	

	@SuppressWarnings("unchecked")
	@Test
	void create_WithEvent_Success() {
		
		ReactiveValueOperations<String, ShipclassRedisEntity> reactiveValueOperations = Mockito.mock(ReactiveValueOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
		Mockito.when(reactiveValueOperations.set(Mockito.anyString(), Mockito.any(ShipclassRedisEntity.class))).thenReturn(Mono.just(true));

		StepVerifier
		.create(shipclassRedisProjectorAdapter.create(event))
		.verifyComplete();
		
		Mockito.verify(reactiveRedisTemplate).opsForValue();

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
		.create(shipclassRedisProjectorAdapter.create(event))
		.expectError(CreateRedisEntityException.class)
		.verify();
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void deleteById_WithEvent_EliminateEntity() {

		ReactiveValueOperations<String, ShipclassRedisEntity> reactiveValueOperations = Mockito.mock(ReactiveValueOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
		Mockito.when(reactiveValueOperations.delete(Mockito.anyString())).thenReturn(Mono.just(true));

		StepVerifier
		.create(shipclassRedisProjectorAdapter.deleteById(event))
		.verifyComplete();
		
		Mockito.verify(reactiveRedisTemplate).opsForValue();
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void deleteById_ThrowExcepcion_ManageError() {

		ReactiveValueOperations<String, ShipclassRedisEntity> reactiveValueOperations = Mockito.mock(ReactiveValueOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
		Mockito.when(reactiveValueOperations.delete(Mockito.anyString())).thenReturn(Mono.error(new RuntimeException()));

		StepVerifier
		.create(shipclassRedisProjectorAdapter.deleteById(event))
		.expectError(RuntimeException.class)
		.verify();
		
		Mockito.verify(reactiveRedisTemplate).opsForValue();
		
	}

}
