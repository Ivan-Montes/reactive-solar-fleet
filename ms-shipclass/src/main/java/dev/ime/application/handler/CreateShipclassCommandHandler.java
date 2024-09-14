package dev.ime.application.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecase.CreateShipclassCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Shipclass;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class CreateShipclassCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Shipclass> readRepositoryPort;
	
	public CreateShipclassCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			ReadRepositoryPort<Shipclass> readRepositoryPort) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.readRepositoryPort = readRepositoryPort;
	}

	@Override
	public Mono<Event> handle(Command command) {
		
		return Mono.justOrEmpty(command)
		        .cast(CreateShipclassCommand.class)
		        .flatMap(this::validateNameAlreadyUsed)
		        .map(this::createEvent)
				.flatMap(eventWriteRepositoryPort::save);
		
	} 

	private Mono<CreateShipclassCommand> validateNameAlreadyUsed(CreateShipclassCommand createdCommand){
		
		return readRepositoryPort.findByName(createdCommand.shipclassName())
                .flatMap(positionItem -> Mono.error(new UniqueValueException(Map.of(GlobalConstants.SHIPCLASS_NAME, createdCommand.shipclassName()))))
                .then(Mono.just(createdCommand));
		
	}	
	
	private Event createEvent(Command command) {		
		
		return new Event(
				GlobalConstants.SHIPCLASS_CAT,
				GlobalConstants.SHIPCLASS_CREATED,
				createEventData(command)
				);
		
	}
	
	private Map<String, Object> createEventData(Command command) {
		
		CreateShipclassCommand createCommand = (CreateShipclassCommand) command;
		return objectMapper.convertValue(createCommand, new TypeReference<Map<String, Object>>() {});

	}
	
}
