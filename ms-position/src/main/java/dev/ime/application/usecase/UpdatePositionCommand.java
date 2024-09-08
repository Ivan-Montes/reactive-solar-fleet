package dev.ime.application.usecase;

import java.util.UUID;

import dev.ime.domain.command.Command;

public record UpdatePositionCommand(
		UUID positionId,
		String positionName,
		String positionDescription
		)implements Command {

}
