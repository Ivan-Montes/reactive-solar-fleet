package dev.ime.application.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecase.UpdateSpacecraftCommand;
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
public class UpdateSpacecraftCommandHandler implements CommandHandler{

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Spacecraft> readRepositoryPort;
	private final RedisCheckerEntityPort redisCheckerEntityPort;
	
	public UpdateSpacecraftCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			ReadRepositoryPort<Spacecraft> readRepositoryPort, RedisCheckerEntityPort redisCheckerEntityPort) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.readRepositoryPort = readRepositoryPort;
		this.redisCheckerEntityPort = redisCheckerEntityPort;
	}
	
	@Override
	public Mono<Event> handle(Command command) {
	
		return Mono.justOrEmpty(command)
				.ofType(UpdateSpacecraftCommand.class)
				.flatMap(this::validateIdExists)
				.flatMap(this::validateNameAlreadyUsed)
				.flatMap(this::validateShipclassIdExists)
				.map(this::createEvent)
				.flatMap(eventWriteRepositoryPort::save);
		
	}

	private Mono<UpdateSpacecraftCommand> validateIdExists(UpdateSpacecraftCommand updateCommand){
		
		return readRepositoryPort.findById(updateCommand.spacecraftId())
				.switchIfEmpty(Mono.error( new ResourceNotFoundException(Map.of(GlobalConstants.SPACECRAFT_ID, updateCommand.spacecraftId().toString()))))						
				.then(Mono.just(updateCommand));	
		
	}

	private Mono<UpdateSpacecraftCommand> validateNameAlreadyUsed(UpdateSpacecraftCommand updateCommand){
		
		return readRepositoryPort.findByName(updateCommand.spacecraftName())
		.map(Spacecraft::getSpacecraftId)
		.filter( idFound -> !idFound.equals(updateCommand.spacecraftId()))
		.flatMap( idFound -> Mono.error(new UniqueValueException(Map.of(GlobalConstants.SPACECRAFT_ID, idFound.toString()))))
        .then(Mono.just(updateCommand));		
		
	}
	
	private Mono<UpdateSpacecraftCommand> validateShipclassIdExists(UpdateSpacecraftCommand updateCommand){
		
		return Mono.justOrEmpty(updateCommand.shipclassId())
				.flatMap(redisCheckerEntityPort::existsById)
				.filter( bool -> bool )
				.switchIfEmpty( Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.SHIPCLASS_ID, updateCommand.shipclassId().toString()))))
				.then(Mono.just(updateCommand));				
		
	}
	
	private Event createEvent(Command command) {		
		
		return new Event(
				GlobalConstants.SPACECRAFT_CAT,
				GlobalConstants.SPACECRAFT_UPDATED,
				createEventData(command)
				);
		
	}
	
	private Map<String, Object> createEventData(Command command) {
		
		UpdateSpacecraftCommand updateCommand = (UpdateSpacecraftCommand) command;
		return objectMapper.convertValue(updateCommand, new TypeReference<Map<String, Object>>() {});

	}

}
