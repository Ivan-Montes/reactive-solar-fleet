package dev.ime.infrastructure.adapter;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;

import dev.ime.application.exception.CreateJpaEntityException;
import dev.ime.application.exception.EmptyResponseException;
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
	public Mono<Void> create(Event event) {
		
		return Mono.justOrEmpty(event.getEventData())		        
		.transform(this::logFlowStep)
		.flatMap(this::createJpaEntity)		        
		.transform(this::logFlowStep)
		.flatMap(this::insertQuery)
		.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.SHIPCLASS_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
		.transform(this::addLogginOptions)
        .then();
		
	}
	
    private Mono<ShipclassJpaEntity> insertQuery(ShipclassJpaEntity entity) {
    	
        return r2dbcTemplate.insert(entity);
        
    }
  
    private Mono<ShipclassJpaEntity> createJpaEntity(Map<String, Object> eventData) {
		
		return Mono.fromCallable( () -> {
			
			UUID shipclassId = extractUuid(eventData, GlobalConstants.SHIPCLASS_ID);
	        String shipclassName = extractString(eventData, GlobalConstants.SHIPCLASS_NAME);
	        String shipclassDescription = extractString(eventData, GlobalConstants.SHIPCLASS_DESC);
			
			return ShipclassJpaEntity
		    		.builder()
		    		.shipclassId(shipclassId)
		    		.shipclassName(shipclassName)
		    		.shipclassDescription(shipclassDescription)
		    		.build();
			
		}).onErrorMap(e -> new CreateJpaEntityException(Map.of( GlobalConstants.SHIPCLASS_CAT, e.getMessage() )));
		
	}

	private UUID extractUuid(Map<String, Object> eventData, String key) {
		
	    return Optional.ofNullable(eventData.get(key))
	                   .map(Object::toString)
	                   .map(UUID::fromString)
	                   .orElse(null);
	    
	}

	private String extractString(Map<String, Object> eventData, String key) {
		
		String value = Optional.ofNullable(eventData.get(key))
	                   .map(Object::toString)
	                   .orElse("");
	    
	    Pattern compiledPattern = Pattern.compile(GlobalConstants.PATTERN_NAME_FULL);
	    Matcher matcher = compiledPattern.matcher(value);
	    if (!matcher.matches()) {
	        throw new IllegalArgumentException(GlobalConstants.EX_ILLEGALARGUMENT_DESC + " OwO " +  key );
	    }

	    return value;
	    
	}
	
	@Override
	public Mono<Void> update(Event event) {
		
		return Mono.justOrEmpty(event.getEventData())		        
		.transform(this::logFlowStep)
		.flatMap(this::createJpaEntity)		        
		.transform(this::logFlowStep)
		.flatMap(this::validateNameAlreadyUsed)	
		.flatMap(this::updateQuery)	
		.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.SHIPCLASS_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
		.transform(this::addLogginOptions)
        .then();
		
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
	
	@Override
	public Mono<Void> deleteById(Event event) {

		return Mono.justOrEmpty(event.getEventData())		        
		.transform(this::logFlowStep)
		.map( evenData -> evenData.get(GlobalConstants.SHIPCLASS_ID))
		.cast(String.class)
		.map(UUID::fromString)			        
		.transform(this::logFlowStep)
		.flatMap( this::deleteByIdQuery )		
		.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.SHIPCLASS_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))		
		.transform(this::addLogginOptions)
		.then();
			
	}

	private Mono<Long> deleteByIdQuery(UUID entityId) {
		
	    return r2dbcTemplate.delete(
	    		Query.query(Criteria.where(GlobalConstants.SHIPCLASS_ID_DB).is(entityId)),
	    		ShipclassJpaEntity.class
	    		);
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
