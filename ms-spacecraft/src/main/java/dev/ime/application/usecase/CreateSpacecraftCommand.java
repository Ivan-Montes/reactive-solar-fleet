package dev.ime.application.usecase;

import java.util.UUID;

import dev.ime.domain.command.Command;

public record CreateSpacecraftCommand(
		UUID spacecraftId,
		String spacecraftName,
		UUID shipclassId
		) implements Command {

}