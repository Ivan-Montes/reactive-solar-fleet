package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.handler.CreateCrewMemberCommandHandler;
import dev.ime.application.handler.DeleteCrewMemberCommandHandler;
import dev.ime.application.handler.UpdateCrewMemberCommandHandler;
import dev.ime.application.usecase.CreateCrewMemberCommand;
import dev.ime.application.usecase.DeleteCrewMemberCommand;
import dev.ime.application.usecase.UpdateCrewMemberCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;

@Component
public class CommandDispatcher {

	private final Map<Class<? extends Command>, CommandHandler> commandHandlers = new HashMap<>();

	public CommandDispatcher(CreateCrewMemberCommandHandler createCommandHandler, UpdateCrewMemberCommandHandler updateCommandHandler,
			DeleteCrewMemberCommandHandler deleteCommandHandler) {
		super();
		commandHandlers.put(CreateCrewMemberCommand.class, createCommandHandler);
		commandHandlers.put(UpdateCrewMemberCommand.class, updateCommandHandler);
		commandHandlers.put(DeleteCrewMemberCommand.class, deleteCommandHandler);
	}

	public CommandHandler  getCommandHandler(Command command) {
		
		Optional<CommandHandler> optHandler = Optional.ofNullable(commandHandlers.get(command.getClass()));
		
		return optHandler.orElseThrow( () -> new IllegalArgumentException(GlobalConstants.MSG_HANDLER_NONE + command.getClass().getName()));	
		
	}
	
}
