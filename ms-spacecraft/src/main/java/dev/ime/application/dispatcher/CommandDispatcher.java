package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.handler.CreateSpacecraftCommandHandler;
import dev.ime.application.handler.DeleteSpacecraftCommandHandler;
import dev.ime.application.handler.UpdateSpacecraftCommandHandler;
import dev.ime.application.usecase.CreateSpacecraftCommand;
import dev.ime.application.usecase.DeleteSpacecraftCommand;
import dev.ime.application.usecase.UpdateSpacecraftCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;

@Component
public class CommandDispatcher {
	
	private final Map<Class<? extends Command>, CommandHandler> commandHandlers = new HashMap<>();

	public CommandDispatcher(CreateSpacecraftCommandHandler createCommandHandler, UpdateSpacecraftCommandHandler updateCommandHandler, DeleteSpacecraftCommandHandler deleteCommandHandler) {
		super();
		commandHandlers.put(CreateSpacecraftCommand.class, createCommandHandler);
		commandHandlers.put(UpdateSpacecraftCommand.class, updateCommandHandler);
		commandHandlers.put(DeleteSpacecraftCommand.class, deleteCommandHandler);
	}

	public CommandHandler getCommandHandler(Command command) {

		Optional<CommandHandler> optHandler = Optional.ofNullable( commandHandlers.get(command.getClass()) );
		
		return optHandler.orElseThrow( () -> new IllegalArgumentException(GlobalConstants.MSG_HANDLER_NONE + command.getClass().getName()));	
		
	}
	
}
