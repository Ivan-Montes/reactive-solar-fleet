package dev.ime.application.handler;


import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecase.CreatePositionCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Position;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class CreatePositionCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Position> readRepositoryPort; 

	public CreatePositionCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			ReadRepositoryPort<Position> readRepositoryPort) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.readRepositoryPort = readRepositoryPort;
	}
	
	@Override
	public Mono<Event> handle(Command command) {
		
	    return Mono.justOrEmpty(command)
	        .cast(CreatePositionCommand.class)
	        .flatMap(this::validateNameAlreadyUsed)
	        .map(this::createEvent)
			.flatMap(eventWriteRepositoryPort::save);
	    
	}	
	
	private Mono<CreatePositionCommand> validateNameAlreadyUsed(CreatePositionCommand createCommand){
		
		return readRepositoryPort.findByName(createCommand.positionName())
                .flatMap(positionItem -> Mono.error(new UniqueValueException(Map.of(GlobalConstants.POSITION_NAME, createCommand.positionName()))))
                .then(Mono.just(createCommand));
		
	}	
	
	private Event createEvent(Command command) {		
		
		return new Event(
				GlobalConstants.POSITION_CAT,
				GlobalConstants.POSITION_CREATED,
				createEventData(command)
				);
		
	}
	
	private Map<String, Object> createEventData(Command command) {
		
		CreatePositionCommand createCommand = (CreatePositionCommand) command;
		return objectMapper.convertValue(createCommand, new TypeReference<Map<String, Object>>() {});

	}
	
}
