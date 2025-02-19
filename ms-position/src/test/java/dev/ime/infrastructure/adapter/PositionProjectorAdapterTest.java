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
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;

import dev.ime.application.exception.CreateJpaEntityException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.event.Event;
import dev.ime.infrastructure.entity.PositionJpaEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PositionProjectorAdapterTest {

	@Mock
	private LoggerUtil loggerUtil;
	@Mock
	private R2dbcEntityTemplate r2dbcTemplate;
	@InjectMocks
	private PositionProjectorAdapter positionProjectorAdapter;	

	private Event event;
	private PositionJpaEntity positionJpaEntity;
	
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.POSITION_CAT;
	private final String eventType = GlobalConstants.POSITION_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private Map<String, Object> eventData;

	private final UUID positionId = UUID.randomUUID();
	private final String positionName = "Apothecary";
	private final String positionDescription = "Person who in the past made and sold medicines";
	
	@BeforeEach
	private void setUp() {
		
		eventData = new HashMap<>();
		eventData.put(GlobalConstants.POSITION_ID, positionId.toString());
		eventData.put(GlobalConstants.POSITION_NAME, positionName);
		eventData.put(GlobalConstants.POSITION_DESC, positionDescription);
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);

		positionJpaEntity = new PositionJpaEntity(
				positionId,
				positionName,
				positionDescription);
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void create_WithEvent_InsertData() {
		
		Mockito.when(r2dbcTemplate.selectOne(Mockito.any(Query.class),Mockito.any(Class.class))).thenReturn(Mono.empty());
		Mockito.when(r2dbcTemplate.insert(Mockito.any(PositionJpaEntity.class))).thenReturn(Mono.just(positionJpaEntity));

		StepVerifier
		.create(positionProjectorAdapter.create(event))
		.verifyComplete();
		
		Mockito.verify(r2dbcTemplate).selectOne(Mockito.any(Query.class),Mockito.any(Class.class));
		Mockito.verify(r2dbcTemplate).insert(Mockito.any(PositionJpaEntity.class));

	}
	
	@Test
	void create_WithBadEvenData_ThrowError() {
		
		eventData.put(GlobalConstants.POSITION_NAME, "");
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);		

		StepVerifier
		.create(positionProjectorAdapter.create(event))
		.expectError(CreateJpaEntityException.class)
		.verify();
		
		Mockito.verify(loggerUtil, Mockito.times(3)).logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

	}

	@SuppressWarnings("unchecked")
	@Test
	void update_WithEvent_UpdateDb(){
		
		Mockito.when(r2dbcTemplate.selectOne(Mockito.any(Query.class),Mockito.any(Class.class))).thenReturn(Mono.empty());
		Mockito.when(r2dbcTemplate.update(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.any(Class.class))).thenReturn(Mono.just(1L));

		StepVerifier
		.create(positionProjectorAdapter.update(event))
		.verifyComplete();
		
		Mockito.verify(r2dbcTemplate).selectOne(Mockito.any(Query.class),Mockito.any(Class.class));
		Mockito.verify(r2dbcTemplate).update(Mockito.any(Query.class),Mockito.any(Update.class),Mockito.any(Class.class));
		Mockito.verify(loggerUtil, Mockito.never()).logSevereAction(Mockito.anyString());

	}

	@SuppressWarnings("unchecked")
	@Test
	void update_WithEvent_ThrowError(){
		
		Mockito.when(r2dbcTemplate.selectOne(Mockito.any(Query.class),Mockito.any(Class.class))).thenReturn(Mono.just(positionJpaEntity));

		StepVerifier
		.create(positionProjectorAdapter.update(event))
		.expectError(UniqueValueException.class)
		.verify();
		
		Mockito.verify(r2dbcTemplate).selectOne(Mockito.any(Query.class),Mockito.any(Class.class));
		Mockito.verify(loggerUtil, Mockito.times(4)).logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

	}

	@Test
	void delete_WithEvent_DeletePosition() {
		
		Mockito.when(r2dbcTemplate.delete(Mockito.any(Query.class),Mockito.any(Class.class))).thenReturn(Mono.just(1L));

		StepVerifier
		.create(positionProjectorAdapter.deleteById(event))
		.verifyComplete();
		
		Mockito.verify(r2dbcTemplate).delete(Mockito.any(Query.class),Mockito.any(Class.class));
		Mockito.verify(loggerUtil, Mockito.never()).logSevereAction(Mockito.anyString());
		 
	}
	
}
