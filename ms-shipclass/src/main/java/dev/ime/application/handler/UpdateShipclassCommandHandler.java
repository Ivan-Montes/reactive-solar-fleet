package dev.ime.application.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecase.UpdateShipclassCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Shipclass;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class UpdateShipclassCommandHandler implements CommandHandler{

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Shipclass> readRepositoryPort;
	
	public UpdateShipclassCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			ReadRepositoryPort<Shipclass> readRepositoryPort) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.readRepositoryPort = readRepositoryPort;
	}

	@Override
	public Mono<Event> handle(Command command) {
	
		return Mono.justOrEmpty(command)
				.ofType(UpdateShipclassCommand.class)
				.flatMap(this::validateIdExists)
				.flatMap(this::validateNameAlreadyUsed)
				.map(this::createEvent)
				.flatMap(eventWriteRepositoryPort::save);
		
	}
	
	private Mono<UpdateShipclassCommand> validateIdExists(UpdateShipclassCommand updateCommand){
		
		return readRepositoryPort.findById(updateCommand.shipclassId())
				.switchIfEmpty(Mono.error( new ResourceNotFoundException(Map.of(GlobalConstants.SHIPCLASS_ID, updateCommand.shipclassId().toString() )) ))						
				.then(Mono.just(updateCommand));	
		
	}

	private Mono<UpdateShipclassCommand> validateNameAlreadyUsed(UpdateShipclassCommand updateCommand){
		
		return readRepositoryPort.findByName(updateCommand.shipclassName())
		.map(Shipclass::getShipclassId)
		.filter( idFound -> !idFound.equals(updateCommand.shipclassId()))
		.flatMap( idFound -> Mono.error(new UniqueValueException(Map.of(GlobalConstants.SHIPCLASS_ID, idFound.toString()))))
        .then(Mono.just(updateCommand));		
		
	}
	
	private Event createEvent(Command command) {		
		
		return new Event(
				GlobalConstants.SHIPCLASS_CAT,
				GlobalConstants.SHIPCLASS_UPDATED,
				createEventData(command)
				);
		
	}
	
	private Map<String, Object> createEventData(Command command) {
		
		UpdateShipclassCommand updateCommand = (UpdateShipclassCommand) command;
		return objectMapper.convertValue(updateCommand, new TypeReference<Map<String, Object>>() {});

	}
}
