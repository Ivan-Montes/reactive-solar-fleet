package dev.ime.application.usecase;

import java.util.UUID;

import dev.ime.domain.command.Command;

public record DeletePositionCommand(
		UUID positionId
		)implements Command {

}
