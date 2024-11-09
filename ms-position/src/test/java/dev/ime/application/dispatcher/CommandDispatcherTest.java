package dev.ime.application.dispatcher;


import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.handler.CreatePositionCommandHandler;
import dev.ime.application.handler.DeletePositionCommandHandler;
import dev.ime.application.handler.UpdatePositionCommandHandler;
import dev.ime.application.usecase.DeletePositionCommand;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;

@ExtendWith(MockitoExtension.class)
class CommandDispatcherTest {

	@Mock
	private CreatePositionCommandHandler createPositionCommandHandler;
	
	@Mock
	private UpdatePositionCommandHandler updatePositionCommandHandler;
	
	@Mock
	private DeletePositionCommandHandler deletePositionCommandHandler;

	@InjectMocks
	private CommandDispatcher commandDispatcher;
	
	private class TestCommand implements Command{};
	
	@Test
	void getCommandHandler_ByDefault_ReturnHandler() {
		
		DeletePositionCommand command = new DeletePositionCommand(UUID.randomUUID());
		
		CommandHandler handler = commandDispatcher.getCommandHandler(command);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(handler).isNotNull()
				);
	}

	@Test
	void getCommandHandler_WithUnknowCommand_ThrowException() {
		
		TestCommand command = new TestCommand();
		
		Exception ex = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, ()-> commandDispatcher.getCommandHandler(command));
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(ex).isNotNull()
				);
	}
	
}
