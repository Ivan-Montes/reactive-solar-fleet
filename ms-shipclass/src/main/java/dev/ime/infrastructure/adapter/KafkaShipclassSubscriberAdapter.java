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
import dev.ime.domain.port.outbound.ExtendedProjectorPort;
import reactor.core.publisher.Mono;

@Component
public class KafkaShipclassSubscriberAdapter implements SubscriberPort{

	private final LoggerUtil loggerUtil;
    private final Map<String, Consumer<Event>> actionsMap;  
    private final BaseProjectorPort baseProjectorPort;
    private final ExtendedProjectorPort extendedProjectorPort;

	public KafkaShipclassSubscriberAdapter(LoggerUtil loggerUtil,
			@Qualifier("shipclassProjectorAdapter")BaseProjectorPort baseProjectorPort, @Qualifier("shipclassProjectorAdapter")ExtendedProjectorPort extendedProjectorPort) {
		super();
		this.loggerUtil = loggerUtil;
		this.baseProjectorPort = baseProjectorPort;
		this.extendedProjectorPort = extendedProjectorPort;
		this.actionsMap = initializeActionsMap();
	}

	private Map<String, Consumer<Event>> initializeActionsMap() {
		
		return Map.of(
                GlobalConstants.SHIPCLASS_CREATED, baseProjectorPort::create,
                GlobalConstants.SHIPCLASS_UPDATED, extendedProjectorPort::update,
                GlobalConstants.SHIPCLASS_DELETED, baseProjectorPort::deleteById
        );
		
	}
	
	@Override	
	@KafkaListener(topics = {GlobalConstants.SHIPCLASS_CREATED, GlobalConstants.SHIPCLASS_UPDATED, GlobalConstants.SHIPCLASS_DELETED}, groupId = "msshipclass-consumer-shipclass")
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
