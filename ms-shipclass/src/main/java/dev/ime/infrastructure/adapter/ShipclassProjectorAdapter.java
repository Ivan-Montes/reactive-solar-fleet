package dev.ime.infrastructure.adapter;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;

import dev.ime.application.exception.UniqueValueException;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.event.Event;
import dev.ime.domain.port.outbound.BaseProjectorPort;
import dev.ime.domain.port.outbound.ExtendedProjectorPort;
import dev.ime.infrastructure.entity.ShipclassJpaEntity;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("shipclassProjectorAdapter")
public class ShipclassProjectorAdapter implements BaseProjectorPort, ExtendedProjectorPort{

	private final LoggerUtil loggerUtil;
	private final R2dbcEntityTemplate r2dbcTemplate;
		
	public ShipclassProjectorAdapter(LoggerUtil loggerUtil, R2dbcEntityTemplate r2dbcTemplate) {
		super();
		this.loggerUtil = loggerUtil;
		this.r2dbcTemplate = r2dbcTemplate;
	}

	@Override
	public void create(Event event) {
		
		Mono.justOrEmpty( createJpaEntity(event) )
		.flatMap(this::insertQuery)
		.transform(this::addLogginOptionsReturnJpa)
		.onErrorResume( e -> Mono.fromRunnable(()-> loggerUtil.logSevereAction(e.getMessage()) ))
        .subscribe();
		
	}
	
    private Mono<ShipclassJpaEntity> insertQuery(ShipclassJpaEntity entity) {
    	
        return r2dbcTemplate.insert(entity);
        
    }
    
    private Mono<ShipclassJpaEntity> addLogginOptionsReturnJpa(Mono<ShipclassJpaEntity> reactiveFlow) {
       
    	return reactiveFlow
            .doOnSuccess(c -> logInfo(GlobalConstants.MSG_FLOW_OK, c.getShipclassId().toString()))
            .doOnError(e -> logInfo(GlobalConstants.MSG_FLOW_ERROR, e.toString()))
            .doFinally(signalType -> logInfo(GlobalConstants.MSG_FLOW_RESULT, signalType.toString()));
  
    }
    
	private ShipclassJpaEntity createJpaEntity(Event event) {
		
		return ShipclassJpaEntity
	    		.builder()
	    		.shipclassId( UUID.fromString( event.getEventData().get(GlobalConstants.SHIPCLASS_ID).toString() ) )
	    		.shipclassName(event.getEventData().get(GlobalConstants.SHIPCLASS_NAME).toString())
	    		.shipclassDescription(event.getEventData().get(GlobalConstants.SHIPCLASS_DESC).toString())
	    		.build();
		
	}
	
	@Override
	public void update(Event event) {
		
		Mono.justOrEmpty(createJpaEntity(event))
		.flatMap(this::validateNameAlreadyUsed)	
		.flatMap(this::updateQuery)	
		.transform(this::addLogginOptionsReturnLong)
		.onErrorResume( e -> Mono.fromRunnable(()-> loggerUtil.logSevereAction(e.getMessage()) ))
		.subscribe();
		
	}

	private Mono<ShipclassJpaEntity> validateNameAlreadyUsed(ShipclassJpaEntity entity) {
	    
		return r2dbcTemplate.selectOne(
				Query.query(Criteria.where(GlobalConstants.SHIPCLASS_NAME_DB).is(entity.getShipclassName())
						.and(GlobalConstants.SHIPCLASS_ID_DB).not(entity.getShipclassId())),
				ShipclassJpaEntity.class)				
				.flatMap( entityFound -> Mono.error(new UniqueValueException(Map.of(GlobalConstants.SHIPCLASS_NAME, entityFound.getShipclassName()))))
				.then(Mono.just(entity));		
	}
	
	private Mono<Long> updateQuery(ShipclassJpaEntity entity) {
		 
		return r2dbcTemplate.update(
				Query.query(Criteria.where(GlobalConstants.SHIPCLASS_ID_DB).is(entity.getShipclassId())),
				Update.update(GlobalConstants.SHIPCLASS_NAME_DB, entity.getShipclassName())
					.set(GlobalConstants.SHIPCLASS_DESC_DB, entity.getShipclassDescription()),
					ShipclassJpaEntity.class);
		
	}
	
	private Mono<Long> addLogginOptionsReturnLong(Mono<Long> reactiveFlow){
	
		return reactiveFlow		
				.doOnSuccess(s -> logInfo(GlobalConstants.MSG_FLOW_OK, GlobalConstants.MSG_MODLINES + s.toString()))
		        .doOnError(e -> logInfo(GlobalConstants.MSG_FLOW_ERROR, e.toString()))
		        .doFinally(signalType -> logInfo(GlobalConstants.MSG_FLOW_RESULT, signalType.toString()));		
	
	}
	
	@Override
	public void deleteById(Event event) {

		Mono.justOrEmpty(event.getEventData().get(GlobalConstants.SHIPCLASS_ID))
		.switchIfEmpty(Mono.error(new IllegalArgumentException(GlobalConstants.SHIPCLASS_ID + GlobalConstants.MSG_REQUIRED)))
		.cast(String.class)
		.map(UUID::fromString)
		.doOnNext( id -> logInfo(GlobalConstants.MSG_FLOW_PROCESS, event.getEventType() + " : " + id.toString()))
		.flatMap( this::deleteByIdQuery )		
		.transform(this::addLogginOptionsReturnLong)
		.onErrorResume( e -> Mono.fromRunnable(()-> loggerUtil.logSevereAction(e.getMessage()) ))
		.subscribe();
			
	}

	private Mono<Long> deleteByIdQuery(UUID entityId) {
		
	    return r2dbcTemplate.delete(
	    		Query.query(Criteria.where(GlobalConstants.SHIPCLASS_ID_DB).is(entityId)),
	    		ShipclassJpaEntity.class
	    		);
	}

	private void logInfo(String action, String entityInfo) {
		
	    loggerUtil.logInfoAction(this.getClass().getSimpleName(), action, entityInfo);
	    
	}
	
}
