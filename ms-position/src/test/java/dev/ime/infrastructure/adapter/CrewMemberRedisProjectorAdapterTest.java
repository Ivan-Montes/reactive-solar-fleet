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
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;

import dev.ime.application.exception.CreateRedisEntityException;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.event.Event;
import dev.ime.infrastructure.entity.CrewMemberRedisEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CrewMemberRedisProjectorAdapterTest {

	@Mock
	private LoggerUtil loggerUtil;
	
	@Mock
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

	@InjectMocks
    private CrewMemberRedisProjectorAdapter crewMemberRedisProjectorAdapter;

	private Event event;
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREWMEMBER_CAT;
	private final String eventType = GlobalConstants.CREWMEMBER_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();

	private CrewMemberRedisEntity crewMemberRedisEntity;
	private final UUID crewMemberId = UUID.randomUUID();
    private final UUID positionId = UUID.randomUUID();
    
	@BeforeEach
	private void setUp() {

		crewMemberRedisEntity = new CrewMemberRedisEntity(crewMemberId, positionId);

		eventData.put(GlobalConstants.CREWMEMBER_ID, crewMemberId.toString());
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
	void create_WithEvent_Success() {
		
		Mockito.when(reactiveRedisTemplate.hasKey(Mockito.anyString())).thenReturn(Mono.just(true));
		ReactiveValueOperations<String, Object> reactiveValueOperations = Mockito.mock(ReactiveValueOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
		Mockito.when(reactiveValueOperations.get(Mockito.anyString())).thenReturn(Mono.just(crewMemberRedisEntity));
		ReactiveSetOperations<String, Object> reactiveSetOperations = Mockito.mock(ReactiveSetOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForSet()).thenReturn(reactiveSetOperations);
		Mockito.when(reactiveSetOperations.remove(Mockito.anyString(), Mockito.any())).thenReturn(Mono.just(1L));
		Mockito.when(reactiveValueOperations.set(Mockito.anyString(), Mockito.any(CrewMemberRedisEntity.class))).thenReturn(Mono.just(true));
		Mockito.when(reactiveSetOperations.add(Mockito.anyString(), Mockito.any())).thenReturn(Mono.just(1L));
		
		StepVerifier
		.create(crewMemberRedisProjectorAdapter.create(event))
		.verifyComplete();
		
		Mockito.verify(reactiveRedisTemplate).hasKey(Mockito.anyString());
		Mockito.verify(reactiveRedisTemplate, Mockito.times(2)).opsForValue();
		Mockito.verify(reactiveRedisTemplate, Mockito.times(2)).opsForSet();
		
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
		.create(crewMemberRedisProjectorAdapter.create(event))
		.expectError(CreateRedisEntityException.class)
		.verify();
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void deleteById_WithEvent_EliminateEntity() {

		Mockito.when(reactiveRedisTemplate.hasKey(Mockito.anyString())).thenReturn(Mono.just(true));
		ReactiveValueOperations<String, Object> reactiveValueOperations = Mockito.mock(ReactiveValueOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
		Mockito.when(reactiveValueOperations.get(Mockito.anyString())).thenReturn(Mono.just(crewMemberRedisEntity));
		ReactiveSetOperations<String, Object> reactiveSetOperations = Mockito.mock(ReactiveSetOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForSet()).thenReturn(reactiveSetOperations);
		Mockito.when(reactiveSetOperations.remove(Mockito.anyString(), Mockito.any())).thenReturn(Mono.just(1L));
		Mockito.when(reactiveValueOperations.delete(Mockito.anyString())).thenReturn(Mono.just(true));

		StepVerifier
		.create(crewMemberRedisProjectorAdapter.deleteById(event))
		.verifyComplete();

		Mockito.verify(reactiveRedisTemplate).hasKey(Mockito.anyString());
		Mockito.verify(reactiveRedisTemplate, Mockito.times(2)).opsForValue();
		Mockito.verify(reactiveRedisTemplate, Mockito.times(1)).opsForSet();
		
	}
	
}
