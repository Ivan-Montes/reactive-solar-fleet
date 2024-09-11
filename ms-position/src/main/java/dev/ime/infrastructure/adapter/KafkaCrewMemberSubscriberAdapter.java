package dev.ime.infrastructure.adapter;

import java.util.Map;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.event.Event;
import dev.ime.domain.port.inbound.SubscriberPort;
import dev.ime.domain.port.outbound.BaseProjectorPort;
import reactor.core.publisher.Mono;

@Component
public class KafkaCrewMemberSubscriberAdapter implements SubscriberPort{

	private final LoggerUtil loggerUtil;
    private final Map<String, Consumer<Event>> actionsMap;  
    private final BaseProjectorPort baseProjectorPort;
    
    public KafkaCrewMemberSubscriberAdapter(LoggerUtil loggerUtil, @Qualifier("crewMemberRedisProjectorAdapter")BaseProjectorPort baseProjectorPort) {
		super();
		this.loggerUtil = loggerUtil;
		this.baseProjectorPort = baseProjectorPort;
		this.actionsMap = initializeActionsMap();
	}
    
	private Map<String, Consumer<Event>> initializeActionsMap() {
		
		return Map.of(
                GlobalConstants.CREWMEMBER_CREATED, baseProjectorPort::create,
                GlobalConstants.CREWMEMBER_UPDATED, baseProjectorPort::create,
                GlobalConstants.CREWMEMBER_DELETED, baseProjectorPort::deleteById
        );
		
	}
	
    @Override
	@KafkaListener(topics = {GlobalConstants.CREWMEMBER_CREATED, GlobalConstants.CREWMEMBER_UPDATED, GlobalConstants.CREWMEMBER_DELETED}, groupId = "msposition-consumer-crewmember")
    public void onMessage(ConsumerRecord<String, Event> consumerRecord) {
    	
		Mono.justOrEmpty(consumerRecord)
		.doOnNext( recordObj -> loggerUtil.logInfoAction(this.getClass().getSimpleName(), recordObj.getClass().getSimpleName(), recordObj.toString()))
		.map(ConsumerRecord::value)
		.flatMap( event -> processEvent(consumerRecord.topic(), event) )
		.onErrorResume( e -> Mono.fromRunnable(()-> loggerUtil.logSevereAction(e.getMessage()) ))
		.subscribe();	
		
	}
	   
    private Mono<Void> processEvent(String topic, Event event) {
 	
     return Mono.justOrEmpty(actionsMap.get(topic))
                .switchIfEmpty(Mono.defer(() -> 
             	   handleDefault(event).thenReturn( e -> {})             	   
                ))
                .flatMap(action -> Mono.fromRunnable(() -> action.accept(event)));
    }
 
	private Mono<Void> handleDefault(Event event) {
		  
		return Mono.fromRunnable( () -> loggerUtil.logInfoAction(this.getClass().getSimpleName(), event.getEventType(), GlobalConstants.MSG_EVENT_ERROR + " : " + event));
	  
	}
	
}
