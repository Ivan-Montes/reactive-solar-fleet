package dev.ime.application.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecase.UpdateCrewMemberCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.CrewMember;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.port.outbound.RedisCheckerEntityPort;
import reactor.core.publisher.Mono;

@Component
public class UpdateCrewMemberCommandHandler implements CommandHandler {
	
	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final RedisCheckerEntityPort redisCheckerEntityPort;
	private final ReadRepositoryPort<CrewMember> readRepositoryPort;
	
	public UpdateCrewMemberCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			RedisCheckerEntityPort redisCheckerEntityPort, ReadRepositoryPort<CrewMember> readRepositoryPort) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.redisCheckerEntityPort = redisCheckerEntityPort;
		this.readRepositoryPort = readRepositoryPort;
	}

	@Override
	public Mono<Event> handle(Command command) {
		
		return Mono.justOrEmpty(command)
				.cast(UpdateCrewMemberCommand.class)
				.flatMap(this::validateCrewMemberExists)
				.flatMap(this::validatePositionExists)
				.flatMap(this::validateSpacecraftExists)
				.map(this::createEvent)
				.flatMap(eventWriteRepositoryPort::save);			
				
	}

	private Mono<UpdateCrewMemberCommand> validatePositionExists(UpdateCrewMemberCommand updateCommand ){
		
		return Mono.justOrEmpty(updateCommand)
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

	private Mono<UpdateCrewMemberCommand> validateSpacecraftExists(UpdateCrewMemberCommand updateCommand ){
		
		return Mono.justOrEmpty(updateCommand)
				.flatMap( command -> {
					
					if ( command.spacecraftId() == null) {
						return Mono.just(updateCommand);
					}
					
					return Mono.just(command)
							.map(UpdateCrewMemberCommand::spacecraftId)
							.flatMap(redisCheckerEntityPort::existsSpacecraft)
							.filter( bool -> bool)
							.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.SPACECRAFT_ID, command.spacecraftId().toString()))))
							.thenReturn(command);
					
				});
		
	}
	
	private Mono<UpdateCrewMemberCommand> validateCrewMemberExists(UpdateCrewMemberCommand updateCommand ){
		
		return Mono.justOrEmpty(updateCommand)
				.map(UpdateCrewMemberCommand::crewMemberId)
				.flatMap(readRepositoryPort::findById)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.CREWMEMBER_ID, updateCommand.crewMemberId().toString()))))
				.thenReturn(updateCommand);
		
	}
		
	private Event createEvent(UpdateCrewMemberCommand updateCommand) {
		
		return new Event(
				GlobalConstants.CREWMEMBER_CAT,
				GlobalConstants.CREWMEMBER_UPDATED,
				createEventData(updateCommand)				
				);
		
	}

	private Map<String, Object> createEventData(UpdateCrewMemberCommand updateCommand) {
		
		return objectMapper.convertValue(updateCommand, new TypeReference<Map<String, Object>>() {});

	}	

}
