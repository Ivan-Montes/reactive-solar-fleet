package dev.ime.application.usecase;

import java.util.UUID;

import dev.ime.domain.command.Command;

public record UpdateCrewMemberCommand(
		UUID crewMemberId,
		String crewMemberName,
		String crewMemberSurname,
		UUID positionId,
		UUID spacecraftId
		) implements Command {

}
