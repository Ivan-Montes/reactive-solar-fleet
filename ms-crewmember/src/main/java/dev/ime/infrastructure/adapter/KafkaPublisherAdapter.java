package dev.ime.infrastructure.adapter;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import dev.ime.application.exception.PublishEventException;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.event.Event;
import dev.ime.domain.port.outbound.PublisherPort;
import reactor.core.publisher.Mono;

@Service
public class KafkaPublisherAdapter implements PublisherPort{

	private final KafkaTemplate<String, Object> kafkaTemplate;	
	private final LoggerUtil loggerUtil;	
	
	public KafkaPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate,
			LoggerUtil loggerUtil) {
		super();
		this.kafkaTemplate = kafkaTemplate;
		this.loggerUtil = loggerUtil;
	}
	
	public Mono<Void> publishEvent(Event event) {
		
	    logInfo(GlobalConstants.MSG_PUBLISH_EVENT, event.toString());
	    return Mono.fromFuture(kafkaTemplate.send(new ProducerRecord<>(event.getEventType(), event)))
	               .doOnSuccess(this::handleSuccess)
	               .onErrorResume(ex -> {
	            	   handleFailure(ex);
	                   return Mono.error(new PublishEventException(Map.of(GlobalConstants.MSG_PUBLISH_FAIL, ex.getMessage())));
	               })
	               .then();
	    
	}
	
	private void handleSuccess(SendResult<String, Object> result) {
		logInfo(GlobalConstants.MSG_PUBLISH_OK, result.getProducerRecord().topic() + "]:[" + result.getProducerRecord().value() );
	}

    private void handleFailure(Throwable ex) {
    	logInfo(GlobalConstants.MSG_PUBLISH_FAIL, ex.getMessage() );
    }

	private void logInfo(String action, String clientInfo) {
		
	    loggerUtil.logInfoAction(this.getClass().getSimpleName(), action, clientInfo);
	    
	}

}
