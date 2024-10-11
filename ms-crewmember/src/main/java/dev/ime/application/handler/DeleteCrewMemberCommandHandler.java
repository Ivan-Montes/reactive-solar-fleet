package dev.ime.application.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecase.DeleteCrewMemberCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.CrewMember;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class DeleteCrewMemberCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ReadRepositoryPort<CrewMember> readRepositoryPort;
	private final ObjectMapper objectMapper;
	
	public DeleteCrewMemberCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort,
			ReadRepositoryPort<CrewMember> readRepositoryPort, ObjectMapper objectMapper) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.readRepositoryPort = readRepositoryPort;
		this.objectMapper = objectMapper;
	}

	@Override
	public Mono<Event> handle(Command command) {
		
		return Mono.justOrEmpty(command)
		.cast(DeleteCrewMemberCommand.class)
		.flatMap(this::validateIdExists)
		.map(this::createEvent)
		.flatMap(eventWriteRepositoryPort::save);		
		
	}

	private Mono<DeleteCrewMemberCommand> validateIdExists(DeleteCrewMemberCommand deleteCommand){
		
		return readRepositoryPort.findById(deleteCommand.crewMemberId())
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.CREWMEMBER_ID,deleteCommand.crewMemberId().toString()))))
				.thenReturn(deleteCommand);
		
	}

	private Event createEvent(DeleteCrewMemberCommand deleteCommand) {
		
		return new Event(
				GlobalConstants.CREWMEMBER_CAT,
				GlobalConstants.CREWMEMBER_DELETED,
				createEventData(deleteCommand)				
				);
		
	}

	private Map<String, Object> createEventData(DeleteCrewMemberCommand deleteCommand) {
		
		return objectMapper.convertValue(deleteCommand, new TypeReference<Map<String, Object>>() {});

	}	
	
}
