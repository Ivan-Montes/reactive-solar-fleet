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
	public Mono<Void> create(Event event) {		
		
		return Mono.justOrEmpty(event.getEventData())		        
			.transform(this::logFlowStep)
			.flatMap(this::createJpaEntity)		        
			.transform(this::logFlowStep)
			.flatMap(this::insertQuery)
			.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.POSITION_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
			.transform(this::addLogginOptions)
	        .then();
		
	}

    private Mono<PositionJpaEntity> insertQuery(PositionJpaEntity entity) {
    	
        return r2dbcTemplate.insert(entity);
        
    }
    
	private Mono<PositionJpaEntity> createJpaEntity(Map<String, Object> eventData) {
		
		return Mono.fromCallable( () -> {
			
			UUID positionId = extractUuid(eventData, GlobalConstants.POSITION_ID);
	        String positionName = extractString(eventData, GlobalConstants.POSITION_NAME);
	        String positionDescription = extractString(eventData, GlobalConstants.POSITION_DESC);
			
			return PositionJpaEntity
		    		.builder()
		    		.positionId(positionId)
		    		.positionName(positionName)
		    		.positionDescription(positionDescription)
		    		.build();
			
		}).onErrorMap(e -> new CreateJpaEntityException(Map.of( GlobalConstants.POSITION_CAT, e.getMessage() )));
		
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
		.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.POSITION_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
		.transform(this::addLogginOptions)
        .then();
		
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
	
	@Override
	public Mono<Void> deleteById(Event event) {
		
		return Mono.justOrEmpty(event.getEventData())		        
		.transform(this::logFlowStep)
		.map( evenData -> evenData.get(GlobalConstants.POSITION_ID))
		.cast(String.class)
		.map(UUID::fromString)			        
		.transform(this::logFlowStep)
		.flatMap( this::deleteByIdQuery )		
		.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.POSITION_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))		
		.transform(this::addLogginOptions)
		.then();
			
	}

	private Mono<Long> deleteByIdQuery(UUID entityId) {
		
	    return r2dbcTemplate.delete(
	    		Query.query(Criteria.where(GlobalConstants.POSITION_ID_DB).is(entityId)),
	    		PositionJpaEntity.class
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
