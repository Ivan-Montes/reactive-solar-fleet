package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;

import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.event.Event;
import dev.ime.domain.port.outbound.BaseProjectorPort;
import dev.ime.infrastructure.entity.SpacecraftRedisEntity;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("spacecraftRedisProjectorAdapter")
public class SpacecraftRedisProjectorAdapter implements BaseProjectorPort{

	private final LoggerUtil loggerUtil;
    private final ReactiveRedisTemplate<String, SpacecraftRedisEntity> reactiveRedisTemplate;

	public SpacecraftRedisProjectorAdapter(LoggerUtil loggerUtil, ReactiveRedisTemplate<String, SpacecraftRedisEntity> reactiveRedisTemplate) {
		super();
		this.loggerUtil = loggerUtil;
		this.reactiveRedisTemplate = reactiveRedisTemplate;
	}

	@Override
	public void create(Event event) {
		
		Mono.justOrEmpty(createEntity(event))
		.flatMap( entity -> reactiveRedisTemplate.opsForValue().set( generateKey(entity.getSpacecraftId() ), entity))
		.transform(this::addLogginOptions)
		.onErrorResume( e -> Mono.fromRunnable( () -> loggerUtil.logSevereAction(e.getMessage())))
		.subscribe();
		
	}
	
	@Override
	public void deleteById(Event event) {
		
		Mono.justOrEmpty(event.getEventData().get(GlobalConstants.SPACECRAFT_ID))
		.switchIfEmpty(Mono.error(new IllegalArgumentException(GlobalConstants.SPACECRAFT_ID + GlobalConstants.MSG_REQUIRED)))
		.cast(String.class)
		.map(UUID::fromString)
		.flatMap( id -> reactiveRedisTemplate.opsForValue().delete( generateKey(id) ))
		.transform(this::addLogginOptions)
		.onErrorResume( e -> Mono.fromRunnable( () -> loggerUtil.logSevereAction(e.getMessage()) ))
		.subscribe();
		
	}	
	
	private SpacecraftRedisEntity createEntity(Event event) {
		
		UUID spacecraftId = UUID.fromString( String.valueOf( event.getEventData().get(GlobalConstants.SPACECRAFT_ID) ) );
		UUID shipclassId = UUID.fromString( String.valueOf( event.getEventData().get(GlobalConstants.SHIPCLASS_ID) ) );

		return new SpacecraftRedisEntity(spacecraftId, shipclassId);
		
	}
	
	private String generateKey(UUID id) {
		
	    return GlobalConstants.SPACECRAFT_CAT  + ":" + id.toString();
	    
	}
	
	private <T> Mono<T> addLogginOptions(Mono<T> reactiveFlow){
	
		return reactiveFlow		
				.doOnSuccess(o -> loggerUtil.logInfoAction(this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_OK,  o.toString()))
		        .doOnError(e -> loggerUtil.logInfoAction(this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_ERROR, e.toString()))
		        .doFinally(signalType -> loggerUtil.logInfoAction(this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_RESULT, signalType.toString()));		
	
	}
	
}
