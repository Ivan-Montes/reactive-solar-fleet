package dev.ime.infrastructure.adapter;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;

import dev.ime.application.exception.CreateRedisEntityException;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.event.Event;
import dev.ime.domain.port.outbound.BaseProjectorPort;
import dev.ime.infrastructure.entity.CrewMemberRedisEntity;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("crewMemberRedisProjectorAdapter")
public class CrewMemberRedisProjectorAdapter  implements BaseProjectorPort{

	private final LoggerUtil loggerUtil;
    private final ReactiveRedisTemplate<String, CrewMemberRedisEntity> reactiveRedisTemplate;
	
    public CrewMemberRedisProjectorAdapter(LoggerUtil loggerUtil,
			ReactiveRedisTemplate<String, CrewMemberRedisEntity> reactiveRedisTemplate) {
		super();
		this.loggerUtil = loggerUtil;
		this.reactiveRedisTemplate = reactiveRedisTemplate;
	}
    
	@Override
	public Mono<Void> create(Event event) {
		
		return Mono.justOrEmpty(event.getEventData())
				.transform(this::logFlowStep)
				.flatMap(this::createEntity)		        
				.transform(this::logFlowStep)
				.flatMap( entity -> reactiveRedisTemplate.opsForValue().set( generateKey(entity.getCrewMemberId() ), entity))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.CREWMEMBER_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform(this::addLogginOptions)
				.then();
		
	}
	
	@Override
	public Mono<Void> deleteById(Event event) {
		
		return Mono.justOrEmpty(event.getEventData().get(GlobalConstants.CREWMEMBER_ID))
		.switchIfEmpty(Mono.error(new IllegalArgumentException(GlobalConstants.CREWMEMBER_ID + GlobalConstants.MSG_REQUIRED)))
		.cast(String.class)
		.map(UUID::fromString)
		.flatMap( id -> reactiveRedisTemplate.opsForValue().delete( generateKey(id) ))
		.transform(this::addLogginOptions)
		.then();
		
	}

	private Mono<CrewMemberRedisEntity> createEntity(Map<String, Object> eventData) {
		
		return Mono.fromCallable( () -> {
			
			UUID crewMemberId = extractUuid(eventData, GlobalConstants.CREWMEMBER_ID);
			UUID positionId = extractUuid(eventData, GlobalConstants.POSITION_ID);

		return new CrewMemberRedisEntity(crewMemberId, positionId);
		
		}).onErrorMap(e -> new CreateRedisEntityException(Map.of( GlobalConstants.CREWMEMBER_CAT, e.getMessage() )));		
		
	}

	private UUID extractUuid(Map<String, Object> eventData, String key) {
		
	    return Optional.ofNullable(eventData.get(key))
	                   .map(Object::toString)
	                   .map(UUID::fromString)
	                   .orElseThrow(() -> new IllegalArgumentException(GlobalConstants.EX_ILLEGALARGUMENT_DESC + ": " + key));
	    
	}
	private String generateKey(UUID id) {
		
	    return GlobalConstants.CREWMEMBER_CAT  + ":" + id.toString();
	    
	}
	
	private <T> Mono<T> addLogginOptions(Mono<T> reactiveFlow){
		
		return reactiveFlow
				.doOnSuccess( success -> loggerUtil.logInfoAction( this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_OK, success instanceof Number? GlobalConstants.MSG_MODLINES + success.toString():success.toString() ) )
		        .doOnError( error -> loggerUtil.logInfoAction( this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_ERROR, error.toString() ) )
		        .doFinally( signal -> loggerUtil.logInfoAction( this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_RESULT, signal.toString() ) );		
			
	}	

	private <T> Mono<T> logFlowStep(Mono<T> reactiveFlow){
		
		return reactiveFlow		
				.doOnNext( data -> loggerUtil.logInfoAction( this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_PROCESS, data.toString() ) );	
			
	}
	
}
