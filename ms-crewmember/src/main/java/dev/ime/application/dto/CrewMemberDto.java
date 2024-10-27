package dev.ime.application.dto;

import java.util.UUID;

import dev.ime.config.GlobalConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CrewMemberDto(
		UUID crewMemberId,
		@NotBlank @Pattern( regexp = GlobalConstants.PATTERN_NAME_FULL ) String crewMemberName,
		@NotBlank @Pattern( regexp = GlobalConstants.PATTERN_NAME_FULL ) String crewMemberSurname,
		UUID positionId,
		UUID spacecraftId
		) {

}
