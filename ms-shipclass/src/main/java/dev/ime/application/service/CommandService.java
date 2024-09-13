package dev.ime.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import dev.ime.application.dispatcher.CommandDispatcher;
import dev.ime.application.dto.ShipclassDto;
import dev.ime.application.usecase.CreateShipclassCommand;
import dev.ime.application.usecase.DeleteShipclassCommand;
import dev.ime.application.usecase.UpdateShipclassCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.port.inbound.CommandServicePort;
import dev.ime.domain.port.outbound.PublisherPort;
import reactor.core.publisher.Mono;

@Service
public class CommandService implements CommandServicePort<ShipclassDto>{

	private final LoggerUtil loggerUtil;
	private final CommandDispatcher commandDispatcher;
	private final PublisherPort publisherPort;
	
	public CommandService(LoggerUtil loggerUtil, CommandDispatcher commandDispatcher,
			PublisherPort publisherPort) {
		super();
		this.loggerUtil = loggerUtil;
		this.commandDispatcher = commandDispatcher;
		this.publisherPort = publisherPort;
	}

	@Override
	public Mono<Event> create(ShipclassDto dto) {
		
		CreateShipclassCommand command = new CreateShipclassCommand(
				UUID.randomUUID(),
				dto.shipclassName(),
				dto.shipclassDescription()
				);
		
		Mono<Event> monoEvent = runHandler(command);
		
        return processEvents(monoEvent);
        
	}
	
	@Override
	public Mono<Event> update(UUID id, ShipclassDto dto) {
		
		UpdateShipclassCommand command = new UpdateShipclassCommand(
				id,
				dto.shipclassName(),
				dto.shipclassDescription()
				);
		
		Mono<Event> monoEvent = runHandler(command);
		
        return processEvents(monoEvent);
        
	}
	
	@Override
	public Mono<Event> deleteById(UUID id) {
		
		DeleteShipclassCommand command = new DeleteShipclassCommand(
				id
				);
		
		Mono<Event> monoEvent = runHandler(command);
		
        return processEvents(monoEvent);
        
	}
	
	private Mono<Event> runHandler(Command command){
		
		CommandHandler handler = commandDispatcher.getCommandHandler(command);
		return handler.handle(command);
		
	}

	private Mono<Event> processEvents(Mono<Event> monoEvent) {
		
	    return monoEvent
	        .doOnNext(event -> {
	            loggerUtil.logInfoAction(this.getClass().getSimpleName(), GlobalConstants.MSG_HANDLER_OK, event.toString());
	            publisherPort.publishEvent(event);
	        });
	
	}
	
}
