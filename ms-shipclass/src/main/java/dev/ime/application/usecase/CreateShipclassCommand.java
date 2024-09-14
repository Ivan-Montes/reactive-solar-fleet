package dev.ime.application.usecase;

import java.util.UUID;

import dev.ime.domain.command.Command;

public record CreateShipclassCommand(
		UUID shipclassId,
		String shipclassName,
		String shipclassDescription		
		) implements Command {

}
