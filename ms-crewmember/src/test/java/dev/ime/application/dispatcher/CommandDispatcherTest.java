package dev.ime.application.dispatcher;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.handler.CreateCrewMemberCommandHandler;
import dev.ime.application.handler.DeleteCrewMemberCommandHandler;
import dev.ime.application.handler.UpdateCrewMemberCommandHandler;
import dev.ime.application.usecase.DeleteCrewMemberCommand;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;

@ExtendWith(MockitoExtension.class)
class CommandDispatcherTest {	

	@Mock
	private CreateCrewMemberCommandHandler createCommandHandler; 
	
	@Mock
	private UpdateCrewMemberCommandHandler updateCommandHandler;

	@Mock
	private DeleteCrewMemberCommandHandler deleteCommandHandler;

	@InjectMocks
	private CommandDispatcher commandDispatcher;	
	
	@Test
	void getCommandHandler_WithCommand_ReturnHandler() {

		CommandHandler handler = commandDispatcher.getCommandHandler(new DeleteCrewMemberCommand(UUID.randomUUID()));
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(handler).isNotNull()
				);
	}
	
	@Test
	void getCommandHandler_WithTestCommand_ReturnIllegalArgumentException() {
		
		class TestCommand implements Command {};
		TestCommand testCommand = new TestCommand();
		
		Exception ex = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, ()-> commandDispatcher.getCommandHandler(testCommand));
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(ex).isNotNull()
				);
	}
	
	
	

}
