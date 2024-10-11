package dev.ime.application.usecase;

import java.util.UUID;

import dev.ime.domain.command.Command;

public record DeleteCrewMemberCommand(
		UUID crewMemberId
		) implements Command {

}
