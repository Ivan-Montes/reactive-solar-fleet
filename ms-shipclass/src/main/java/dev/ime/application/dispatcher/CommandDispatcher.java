package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.handler.CreateShipclassCommandHandler;
import dev.ime.application.handler.DeleteShipclassCommandHandler;
import dev.ime.application.handler.UpdateShipclassCommandHandler;
import dev.ime.application.usecase.CreateShipclassCommand;
import dev.ime.application.usecase.UpdateShipclassCommand;
import dev.ime.application.usecase.DeleteShipclassCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;

@Component
public class CommandDispatcher {

	private final Map<Class<? extends Command>, CommandHandler> commandHandlers = new HashMap<>();

	public CommandDispatcher(CreateShipclassCommandHandler createCommandHandler, UpdateShipclassCommandHandler updateCommandHandler, DeleteShipclassCommandHandler deleteCommandHandler) {
		super();
		commandHandlers.put(CreateShipclassCommand.class, createCommandHandler);
		commandHandlers.put(UpdateShipclassCommand.class, updateCommandHandler);
		commandHandlers.put(DeleteShipclassCommand.class, deleteCommandHandler);
	}
	
	public CommandHandler getCommandHandler(Command command) {

		Optional<CommandHandler> optHandler = Optional.ofNullable( commandHandlers.get(command.getClass()) );
		
		return optHandler.orElseThrow( () -> new IllegalArgumentException(GlobalConstants.MSG_HANDLER_NONE + command.getClass().getName()));	
		
	}
	
}
