package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.handler.CreatePositionCommandHandler;
import dev.ime.application.handler.DeletePositionCommandHandler;
import dev.ime.application.handler.UpdatePositionCommandHandler;
import dev.ime.application.usecase.CreatePositionCommand;
import dev.ime.application.usecase.DeletePositionCommand;
import dev.ime.application.usecase.UpdatePositionCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;

@Component
public class CommandDispatcher {

	private final Map<Class<? extends Command>, CommandHandler> commandHandlers = new HashMap<>();

	public CommandDispatcher(CreatePositionCommandHandler createCommandHandler, UpdatePositionCommandHandler updateCommandHandler, DeletePositionCommandHandler deleteCommandHandler) {
		super();
		commandHandlers.put(CreatePositionCommand.class, createCommandHandler);
		commandHandlers.put(UpdatePositionCommand.class, updateCommandHandler);
		commandHandlers.put(DeletePositionCommand.class, deleteCommandHandler);
	}
	
	public CommandHandler getCommandHandler(Command command) {

		Optional<CommandHandler> optHandler = Optional.ofNullable( commandHandlers.get(command.getClass()) );
		
		return optHandler.orElseThrow( () -> new IllegalArgumentException(GlobalConstants.MSG_HANDLER_NONE + command.getClass().getName()));	
		
	}
	
}
