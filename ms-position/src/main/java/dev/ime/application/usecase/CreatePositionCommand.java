package dev.ime.application.usecase;

import java.util.UUID;

import dev.ime.domain.command.Command;

public record CreatePositionCommand(
		UUID positionId,
		String positionName,
		String positionDescription
		) implements Command {

}
