package dev.ime.application.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecase.DeleteShipclassCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Shipclass;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class DeleteShipclassCommandHandler  implements CommandHandler{

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Shipclass> readRepositoryPort;	
	private final RedisCheckerEntityPort redisCheckerEntityPort;
	
	public DeleteShipclassCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			ReadRepositoryPort<Shipclass> readRepositoryPort, RedisCheckerEntityPort redisCheckerEntityPort) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.readRepositoryPort = readRepositoryPort;
		this.redisCheckerEntityPort = redisCheckerEntityPort;
	}

	@Override
	public Mono<Event> handle(Command command) {

		return Mono.justOrEmpty(command)
		.cast(DeleteShipclassCommand.class)
		.flatMap(this::validateIdExists)
		.flatMap(this::validateShipclassIsInSpacecraft)
		.map(this::createEvent)
		.flatMap(eventWriteRepositoryPort::save);		
		
	}
	
	private Mono<DeleteShipclassCommand> validateIdExists(DeleteShipclassCommand deleteCommand){
		
		return readRepositoryPort.findById(deleteCommand.shipclassId())
				.switchIfEmpty(
						Mono.error(new ResourceNotFoundException(
															Map.of(
																GlobalConstants.SHIPCLASS_ID, 
																deleteCommand.shipclassId().toString()
																))))
				.thenReturn(deleteCommand);	
		
	}
	
	private Mono<DeleteShipclassCommand> validateShipclassIsInSpacecraft(DeleteShipclassCommand deleteCommand){
		
		return Mono.justOrEmpty(deleteCommand.shipclassId())
				.flatMap(redisCheckerEntityPort::existsAnyByShipclassId)
				.filter( bool -> !bool )
				.switchIfEmpty(Mono.error(new EntityAssociatedException(
												Map.of(
														GlobalConstants.SHIPCLASS_ID, 
														deleteCommand.shipclassId().toString()))))
				.thenReturn(deleteCommand);	
			
	}
	
	private Event createEvent(Command command) {
		
		return new Event(
				GlobalConstants.SHIPCLASS_CAT,
				GlobalConstants.SHIPCLASS_DELETED,
				createEventData(command)
				);
	}

	private Map<String, Object> createEventData(Command command) {
		
		DeleteShipclassCommand deleteCommand = (DeleteShipclassCommand) command;
		return objectMapper.convertValue(deleteCommand, new TypeReference<Map<String, Object>>() {});
	
	}
	
}
