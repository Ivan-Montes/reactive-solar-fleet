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
import dev.ime.infrastructure.entity.PositionJpaEntity;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("positionProjectorAdapter")
public class PositionProjectorAdapter implements BaseProjectorPort, ExtendedProjectorPort{

	private final LoggerUtil loggerUtil;
	private final R2dbcEntityTemplate r2dbcTemplate;
	
	public PositionProjectorAdapter(LoggerUtil loggerUtil, R2dbcEntityTemplate r2dbcTemplate) {
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

    private Mono<PositionJpaEntity> insertQuery(PositionJpaEntity entity) {
    	
        return r2dbcTemplate.insert(entity);
        
    }
    
    private Mono<PositionJpaEntity> addLogginOptionsReturnJpa(Mono<PositionJpaEntity> reactiveFlow) {
       
    	return reactiveFlow
            .doOnSuccess(c -> logInfo(GlobalConstants.MSG_FLOW_OK, c.getPositionId().toString()))
            .doOnError(e -> logInfo(GlobalConstants.MSG_FLOW_ERROR, e.toString()))
            .doFinally(signalType -> logInfo(GlobalConstants.MSG_FLOW_RESULT, signalType.toString()));
  
    }
    
	private PositionJpaEntity createJpaEntity(Event event) {
		
		return PositionJpaEntity
	    		.builder()
	    		.positionId( UUID.fromString( event.getEventData().get(GlobalConstants.POSITION_ID).toString() ) )
	    		.positionName(event.getEventData().get(GlobalConstants.POSITION_NAME).toString())
	    		.positionDescription(event.getEventData().get(GlobalConstants.POSITION_DESC).toString())
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

	private Mono<PositionJpaEntity> validateNameAlreadyUsed(PositionJpaEntity entity) {
	    
		return r2dbcTemplate.selectOne(
				Query.query(Criteria.where(GlobalConstants.POSITION_NAME_DB).is(entity.getPositionName())
						.and(GlobalConstants.POSITION_ID_DB).not(entity.getPositionId())),
				PositionJpaEntity.class)				
				.flatMap( entityFound -> Mono.error(new UniqueValueException(Map.of(GlobalConstants.POSITION_NAME, entityFound.getPositionName()))))
				.then(Mono.just(entity));		
	}
	
	private Mono<Long> updateQuery(PositionJpaEntity entity) {
		 
		return r2dbcTemplate.update(
				Query.query(Criteria.where(GlobalConstants.POSITION_ID_DB).is(entity.getPositionId())),
				Update.update(GlobalConstants.POSITION_NAME_DB, entity.getPositionName())
					.set(GlobalConstants.POSITION_DESC_DB, entity.getPositionDescription()),
				PositionJpaEntity.class);
		
	}
	
	private Mono<Long> addLogginOptionsReturnLong(Mono<Long> reactiveFlow){
	
		return reactiveFlow		
				.doOnSuccess(s -> logInfo(GlobalConstants.MSG_FLOW_OK, GlobalConstants.MSG_MODLINES + s.toString()))
		        .doOnError(e -> logInfo(GlobalConstants.MSG_FLOW_ERROR, e.toString()))
		        .doFinally(signalType -> logInfo(GlobalConstants.MSG_FLOW_RESULT, signalType.toString()));		
	
	}
	
	@Override
	public void deleteById(Event event) {
		
		Mono.justOrEmpty(event.getEventData().get(GlobalConstants.POSITION_ID))
		.switchIfEmpty(Mono.error(new IllegalArgumentException(GlobalConstants.POSITION_ID + GlobalConstants.MSG_REQUIRED)))
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
	    		Query.query(Criteria.where(GlobalConstants.POSITION_ID_DB).is(entityId)),
	    		PositionJpaEntity.class
	    		);
	}
	
	private void logInfo(String action, String entityInfo) {
		
	    loggerUtil.logInfoAction(this.getClass().getSimpleName(), action, entityInfo);
	    
	}
	
}
