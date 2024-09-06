package dev.ime.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import dev.ime.application.dispatcher.CommandDispatcher;
import dev.ime.application.dto.PositionDto;
import dev.ime.application.usecase.CreatePositionCommand;
import dev.ime.application.usecase.DeletePositionCommand;
import dev.ime.application.usecase.UpdatePositionCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.port.inbound.CommandServicePort;
import dev.ime.domain.port.outbound.PublisherPort;
import reactor.core.publisher.Mono;

@Service
public class CommandService implements CommandServicePort<PositionDto>{

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
	public Mono<Event> create(PositionDto dto) {
		
		CreatePositionCommand command = new CreatePositionCommand(
				UUID.randomUUID(),
				dto.positionName(),
				dto.positionDescription()
				);
		
		Mono<Event> monoEvent = runHandler(command);
		
        return processEvents(monoEvent);
        
	}
	
	@Override
	public Mono<Event> update(UUID id, PositionDto dto) {
		
		UpdatePositionCommand command = new UpdatePositionCommand(
				id,
				dto.positionName(),
				dto.positionDescription()
				);
		
		Mono<Event> monoEvent = runHandler(command);
		
        return processEvents(monoEvent);
        
	}
	
	@Override
	public Mono<Event> deleteById(UUID id) {
		
		DeletePositionCommand command = new DeletePositionCommand(
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
