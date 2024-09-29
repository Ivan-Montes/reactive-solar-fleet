package dev.ime.application.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecase.DeleteSpacecraftCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Spacecraft;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class DeleteSpacecraftCommandHandler  implements CommandHandler{

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Spacecraft> readRepositoryPort;
	private final RedisCheckerEntityPort redisCheckerEntityPort;

	public DeleteSpacecraftCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			ReadRepositoryPort<Spacecraft> readRepositoryPort,
			RedisCheckerEntityPort redisCheckerEntityPort) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.readRepositoryPort = readRepositoryPort;
		this.redisCheckerEntityPort = redisCheckerEntityPort;
	}

	@Override
	public Mono<Event> handle(Command command) {

		return Mono.justOrEmpty(command)
		.cast(DeleteSpacecraftCommand.class)
		.flatMap(this::validateIdExists)
		.flatMap(this::validateSpacecrafIsInCrew)
		.map(this::createEvent)
		.flatMap(eventWriteRepositoryPort::save);		
		
	}

	private Mono<DeleteSpacecraftCommand> validateIdExists(DeleteSpacecraftCommand deleteCommand){
		
		return readRepositoryPort.findById(deleteCommand.spacecraftId())
				.switchIfEmpty(
						Mono.error(new ResourceNotFoundException(
															Map.of(
																GlobalConstants.SPACECRAFT_ID, 
																deleteCommand.spacecraftId().toString()
																))))
				.then(Mono.just(deleteCommand));	
		
	}
	
	private Mono<DeleteSpacecraftCommand> validateSpacecrafIsInCrew(DeleteSpacecraftCommand deleteCommand){
		
		return Mono.justOrEmpty(deleteCommand.spacecraftId())
				.flatMap(redisCheckerEntityPort::existsAnySpacecrafInCrewMember)
				.filter( bool -> !bool )
				.switchIfEmpty(Mono.error(new EntityAssociatedException(Map.of(GlobalConstants.SPACECRAFT_ID, deleteCommand.spacecraftId().toString()))))
				.thenReturn(deleteCommand);	
		
	}	

	private Event createEvent(Command command) {		
		
		return new Event(
				GlobalConstants.SPACECRAFT_CAT,
				GlobalConstants.SPACECRAFT_DELETED,
				createEventData(command)
				);
		
	}
	
	private Map<String, Object> createEventData(Command command) {
		
		DeleteSpacecraftCommand deleteCommand = (DeleteSpacecraftCommand) command;
		return objectMapper.convertValue(deleteCommand, new TypeReference<Map<String, Object>>() {});

	}

}
