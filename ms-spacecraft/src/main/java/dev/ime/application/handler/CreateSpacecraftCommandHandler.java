package dev.ime.application.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecase.CreateSpacecraftCommand;
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
public class CreateSpacecraftCommandHandler  implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Spacecraft> readRepositoryPort;
	private final RedisCheckerEntityPort redisCheckerEntityPort;
	
	public CreateSpacecraftCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
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
		        .cast(CreateSpacecraftCommand.class)
		        .flatMap(this::validateNameAlreadyUsed)
		        .flatMap(this::validateShipclassIdExists)
		        .map(this::createEvent)
				.flatMap(eventWriteRepositoryPort::save);
		
	}

	private Mono<CreateSpacecraftCommand> validateNameAlreadyUsed(CreateSpacecraftCommand createdCommand){
		
		return readRepositoryPort.findByName(createdCommand.spacecraftName())
                .flatMap(positionItem -> Mono.error(new UniqueValueException(Map.of(GlobalConstants.SPACECRAFT_NAME, createdCommand.spacecraftName()))))
                .then(Mono.just(createdCommand));
		
	}
	
	private Mono<CreateSpacecraftCommand> validateShipclassIdExists(CreateSpacecraftCommand createdCommand){
		
		return Mono.justOrEmpty(createdCommand.shipclassId())
				.flatMap(redisCheckerEntityPort::existsById)
				.filter( bool -> bool )
				.switchIfEmpty( Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.SHIPCLASS_ID, createdCommand.shipclassId().toString()))))
				.then(Mono.just(createdCommand));	
	}
	
	private Event createEvent(Command command) {		
		
		return new Event(
				GlobalConstants.SPACECRAFT_CAT,
				GlobalConstants.SPACECRAFT_CREATED,
				createEventData(command)
				);
		
	}
	
	private Map<String, Object> createEventData(Command command) {
		
		CreateSpacecraftCommand createCommand = (CreateSpacecraftCommand) command;
		return objectMapper.convertValue(createCommand, new TypeReference<Map<String, Object>>() {});

	}

}
