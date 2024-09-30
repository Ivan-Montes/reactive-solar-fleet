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
import dev.ime.infrastructure.entity.ShipclassRedisEntity;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("shipclassRedisProjectorAdapter")
public class ShipclassRedisProjectorAdapter implements BaseProjectorPort{

	private final LoggerUtil loggerUtil;
    private final ReactiveRedisTemplate<String, ShipclassRedisEntity> reactiveRedisTemplate;

	public ShipclassRedisProjectorAdapter(LoggerUtil loggerUtil, ReactiveRedisTemplate<String, ShipclassRedisEntity> reactiveRedisTemplate) {
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
		        .flatMap( entity -> reactiveRedisTemplate.opsForValue().set( generateKey(entity.getShipclassId() ), entity))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.SHIPCLASS_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform(this::addLogginOptions)
				.then();
	}
	
	@Override
	public Mono<Void> deleteById(Event event) {

		return Mono.justOrEmpty(event.getEventData())
				.transform(this::logFlowStep)
				.map( evenData -> evenData.get(GlobalConstants.SHIPCLASS_ID))
				.cast(String.class)
				.map(UUID::fromString)			        
				.transform(this::logFlowStep)
				.flatMap( id -> reactiveRedisTemplate.opsForValue().delete( generateKey(id) ))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.SHIPCLASS_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform(this::addLogginOptions)
				.then();
		
	}	
	
	private Mono<ShipclassRedisEntity> createEntity(Map<String, Object> eventData) {
		
		return Mono.fromCallable( () -> {
			
	        UUID shipclassId = extractUuid(eventData, GlobalConstants.SHIPCLASS_ID);
	        
			return new ShipclassRedisEntity(shipclassId);
			
		}).onErrorMap(e -> new CreateRedisEntityException(Map.of( GlobalConstants.SHIPCLASS_CAT, e.getMessage() )));		
		
	}

	private UUID extractUuid(Map<String, Object> eventData, String key) {
		
	    return Optional.ofNullable(eventData.get(key))
	                   .map(Object::toString)
	                   .map(UUID::fromString)
	                   .orElseThrow(() -> new IllegalArgumentException(GlobalConstants.EX_ILLEGALARGUMENT_DESC + ": " + key));
	    
	}
	
	private String generateKey(UUID id) {
		
	    return GlobalConstants.SHIPCLASS_CAT  + ":" + id.toString();
	    
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
