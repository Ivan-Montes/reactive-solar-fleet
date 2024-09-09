package dev.ime.application.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecase.UpdatePositionCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Position;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class UpdatePositionCommandHandler implements CommandHandler{
	
	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Position> readRepositoryPort;
	
	public UpdatePositionCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			ReadRepositoryPort<Position> readRepositoryPort) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.readRepositoryPort = readRepositoryPort;
	}

	@Override
	public Mono<Event> handle(Command command) {
		
		return Mono.justOrEmpty(command)
		.ofType(UpdatePositionCommand.class)
		.flatMap(this::validateIdExists)
		.flatMap(this::validateNameAlreadyUsed)
		.map(this::createEvent)
		.flatMap(eventWriteRepositoryPort::save);
		
	}
	
	private Mono<UpdatePositionCommand> validateIdExists(UpdatePositionCommand updateCommand){
		
		return readRepositoryPort.findById(updateCommand.positionId())
				.switchIfEmpty(Mono.error( new ResourceNotFoundException(Map.of(GlobalConstants.POSITION_ID, updateCommand.positionId().toString() )) ))						
				.then(Mono.just(updateCommand));	
		
	}
 
	private Mono<UpdatePositionCommand> validateNameAlreadyUsed(UpdatePositionCommand updatePositionCommand){
		
		return readRepositoryPort.findByName(updatePositionCommand.positionName())
		.map(Position::getPositionId)
		.filter( idFound -> !idFound.equals(updatePositionCommand.positionId()))
		.flatMap( idFound -> Mono.error(new UniqueValueException(Map.of(GlobalConstants.POSITION_NAME, idFound.toString()))))
        .then(Mono.just(updatePositionCommand));		
		
	}	

	private Event createEvent(Command command) {		
		
		return new Event(
				GlobalConstants.POSITION_CAT,
				GlobalConstants.POSITION_UPDATED,
				createEventData(command)
				);
		
	}

	private Map<String, Object> createEventData(Command command) {
		
		UpdatePositionCommand updatePositionCommand = (UpdatePositionCommand) command;
		return objectMapper.convertValue(updatePositionCommand, new TypeReference<Map<String, Object>>() {});
	
	}

}
