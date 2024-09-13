package dev.ime.application.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecase.DeletePositionCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Position;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class DeletePositionCommandHandler implements CommandHandler{

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Position> readRepositoryPort;
	private final RedisCheckerEntityPort redisCheckerEntityPort;

	public DeletePositionCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			ReadRepositoryPort<Position> readRepositoryPort, RedisCheckerEntityPort redisCheckerEntityPort) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.readRepositoryPort = readRepositoryPort;
		this.redisCheckerEntityPort = redisCheckerEntityPort;
	}

	@Override
	public Mono<Event> handle(Command command) {
		
		return Mono.justOrEmpty(command)
		.cast(DeletePositionCommand.class)
		.flatMap(this::validateIdExists)
		.flatMap(this::validatePositionIsInCrew)
		.map(this::createEvent)
		.flatMap(eventWriteRepositoryPort::save);		
		
	}
	
	private Mono<DeletePositionCommand> validateIdExists(DeletePositionCommand deleteCommand){
	
		return readRepositoryPort.findById(deleteCommand.positionId())
				.switchIfEmpty(
						Mono.error(new ResourceNotFoundException(
															Map.of(
																GlobalConstants.POSITION_ID, 
																deleteCommand.positionId().toString()
																))))
				.then(Mono.just(deleteCommand));	
		
	}
	
	private Mono<DeletePositionCommand> validatePositionIsInCrew(DeletePositionCommand deleteCommand){
		
		return Mono.justOrEmpty(deleteCommand.positionId())
				.flatMap(redisCheckerEntityPort::existsAnyPositionInCrewMember)
				.filter( bool -> !bool )
				.switchIfEmpty(Mono.error(new EntityAssociatedException(Map.of(GlobalConstants.POSITION_ID, deleteCommand.positionId().toString()))))
				.thenReturn(deleteCommand);	
		
	}
	
	private Event createEvent(Command command) {
		
		return new Event(
				GlobalConstants.POSITION_CAT,
				GlobalConstants.POSITION_DELETED,
				createEventData(command)
				);
	}

	private Map<String, Object> createEventData(Command command) {
		
		DeletePositionCommand deletePositionCommand = (DeletePositionCommand) command;
		return objectMapper.convertValue(deletePositionCommand, new TypeReference<Map<String, Object>>() {});
	
	}
	
}
