package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;

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
	public void create(Event event) {

		Mono.justOrEmpty(createEntity(event))
		.flatMap( entity -> reactiveRedisTemplate.opsForValue().set( generateKey(entity.getCrewMemberId() ), entity))
		.transform(this::addLogginOptions)
		.onErrorResume( e -> Mono.fromRunnable( () -> loggerUtil.logSevereAction(e.getMessage())))
		.subscribe();
		
	}
	
	@Override
	public void deleteById(Event event) {
		
		Mono.justOrEmpty(event.getEventData().get(GlobalConstants.CREWMEMBER_ID))
		.switchIfEmpty(Mono.error(new IllegalArgumentException(GlobalConstants.CREWMEMBER_ID + GlobalConstants.MSG_REQUIRED)))
		.cast(String.class)
		.map(UUID::fromString)
		.flatMap( id -> reactiveRedisTemplate.opsForValue().delete( generateKey(id) ))
		.transform(this::addLogginOptions)
		.onErrorResume( e -> Mono.fromRunnable( () -> loggerUtil.logSevereAction(e.getMessage()) ))
		.subscribe();
		
	}

	private CrewMemberRedisEntity createEntity(Event event) {
		
		UUID crewMemberId = UUID.fromString( String.valueOf( event.getEventData().get(GlobalConstants.CREWMEMBER_ID) ) );
		UUID positionId = UUID.fromString( String.valueOf( event.getEventData().get(GlobalConstants.POSITION_ID) ) );

		return new CrewMemberRedisEntity(crewMemberId, positionId);
		
	}
	
	private String generateKey(UUID id) {
		
	    return GlobalConstants.CREWMEMBER_CAT  + ":" + id.toString();
	    
	}
	
	private <T> Mono<T> addLogginOptions(Mono<T> reactiveFlow){
	
		return reactiveFlow		
				.doOnSuccess(o -> loggerUtil.logInfoAction(this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_OK,  o.toString()))
		        .doOnError(e -> loggerUtil.logInfoAction(this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_ERROR, e.toString()))
		        .doFinally(signalType -> loggerUtil.logInfoAction(this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_RESULT, signalType.toString()));		
	
	}
	
}
