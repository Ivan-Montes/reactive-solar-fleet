package dev.ime.application.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecase.CreateCrewMemberCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import reactor.core.publisher.Mono;

@Component
public class CreateCrewMemberCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final RedisCheckerEntityPort redisCheckerEntityPort;
	
	public CreateCrewMemberCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			RedisCheckerEntityPort redisCheckerEntityPort) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.redisCheckerEntityPort = redisCheckerEntityPort;
	}

	@Override
	public Mono<Event> handle(Command command) {
		
		return Mono.justOrEmpty(command)
				.cast(CreateCrewMemberCommand.class)
				.flatMap(this::validatePositionExists)
				.flatMap(this::validateSpacecraftExists)
				.map(this::createEvent)
				.flatMap(eventWriteRepositoryPort::save);
				
	}

	private Mono<CreateCrewMemberCommand> validatePositionExists(CreateCrewMemberCommand createCommand) {
	    
		return Mono.justOrEmpty(createCommand)
	        .flatMap(command -> {
	        	
	            if (command.positionId() == null) {
	                return Mono.just(command); 
	            }
	            
	            return Mono.just(command.positionId())
	                .flatMap(redisCheckerEntityPort::existsPosition)
	                .filter(bool -> bool)
	                .switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.POSITION_ID, command.positionId().toString()))))
	                .thenReturn(command);
	            
	        });
		
	}

	private Mono<CreateCrewMemberCommand> validateSpacecraftExists(CreateCrewMemberCommand createCommand ){
		
		return Mono.justOrEmpty(createCommand)
				.flatMap( command -> {
					
					if ( command.spacecraftId() == null) {
						return Mono.just(createCommand);
					}
					
					return Mono.just(command)
							.map(CreateCrewMemberCommand::spacecraftId)
							.flatMap(redisCheckerEntityPort::existsSpacecraft)
							.filter( bool -> bool)
							.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.SPACECRAFT_ID, createCommand.spacecraftId().toString()))))
							.thenReturn(command);
					
				});
	}	
	
	private Event createEvent(CreateCrewMemberCommand createCommand) {
		
		return new Event(
				GlobalConstants.CREWMEMBER_CAT,
				GlobalConstants.CREWMEMBER_CREATED,
				createEventData(createCommand)				
				);
		
	}

	private Map<String, Object> createEventData(CreateCrewMemberCommand createCommand) {
		
		return objectMapper.convertValue(createCommand, new TypeReference<Map<String, Object>>() {});

	}	
	
}
