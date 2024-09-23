package dev.ime.infrastructure.adapter;

import java.util.Map;
import java.util.function.Function;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.exception.ValidationException;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.event.Event;
import dev.ime.domain.port.inbound.SubscriberPort;
import dev.ime.domain.port.outbound.BaseProjectorPort;
import reactor.core.publisher.Mono;

@Component
public class KafkaCrewMemberSubscriberAdapter implements SubscriberPort{

	private final LoggerUtil loggerUtil;
    private final Map<String, Function<Event, Mono<?>>> actionsMap;  
    private final BaseProjectorPort baseProjectorPort;
    
    public KafkaCrewMemberSubscriberAdapter(LoggerUtil loggerUtil, @Qualifier("crewMemberRedisProjectorAdapter")BaseProjectorPort baseProjectorPort) {
		super();
		this.loggerUtil = loggerUtil;
		this.baseProjectorPort = baseProjectorPort;
		this.actionsMap = initializeActionsMap();
	}
    
	private Map<String, Function<Event, Mono<?>>> initializeActionsMap() {
		
		return Map.of(
                GlobalConstants.CREWMEMBER_CREATED, baseProjectorPort::create,
                GlobalConstants.CREWMEMBER_UPDATED, baseProjectorPort::create,
                GlobalConstants.CREWMEMBER_DELETED, baseProjectorPort::deleteById
        );
		
	}
	
    @Override
	@KafkaListener(topics = {GlobalConstants.CREWMEMBER_CREATED, GlobalConstants.CREWMEMBER_UPDATED, GlobalConstants.CREWMEMBER_DELETED}, groupId = "msposition-consumer-crewmember")
    public Mono<Void> onMessage(ConsumerRecord<String, Event> consumerRecord) {
    	
    	return Mono.justOrEmpty(consumerRecord)
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(this.getClass().getSimpleName(), GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform(this::logFlowStep)
				.flatMap(this::validateTopic)
				.flatMap(this::validateValue)
			    .transform(this::logFlowStep)
				.flatMap(this::processEvent)
				.onErrorResume( error -> Mono.fromRunnable(()-> loggerUtil.logSevereAction( this.getClass().getSimpleName() + "]:[" +  GlobalConstants.MSG_FLOW_ERROR + "]:[" + error.toString() ) ))
				.then();
		
	}
	
	private Mono<ConsumerRecord<String, Event>> validateTopic(ConsumerRecord<String, Event> consumer ){
		
		return Mono.justOrEmpty(consumer.topic())
				.filter( topic -> topic != null && !topic.isEmpty() )
				.switchIfEmpty(Mono.error(new ValidationException(Map.of(this.getClass().getSimpleName(), GlobalConstants.EX_VALIDATION_DESC))))
				.thenReturn(consumer);				
				
	}
	
	private Mono<ConsumerRecord<String, Event>> validateValue(ConsumerRecord<String, Event> consumer ){
		
		return Mono.justOrEmpty(consumer.value())
				.switchIfEmpty(Mono.error(new ValidationException(Map.of(this.getClass().getSimpleName(), GlobalConstants.EX_VALIDATION_DESC))))
				.thenReturn(consumer);				
				
	}
	
    private Mono<Void> processEvent(ConsumerRecord<String, Event> consumer ){
    	
    	return Mono.justOrEmpty(consumer.topic())
    	.map(actionsMap::get)
		.switchIfEmpty(Mono.error(new IllegalArgumentException(this.getClass().getSimpleName() + GlobalConstants.MSG_HANDLER_NONE)))
		.flatMap( function -> function.apply(consumer.value()))
    	.then();
    	
    }

	private <T> Mono<T> logFlowStep(Mono<T> reactiveFlow){
		
		return reactiveFlow		
				.doOnNext( data -> loggerUtil.logInfoAction( this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_PROCESS, data.toString() ) );	
			
	}
	
}
