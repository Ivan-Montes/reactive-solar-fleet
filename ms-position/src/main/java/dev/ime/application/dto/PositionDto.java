package dev.ime.application.dto;

import java.util.UUID;

import dev.ime.config.GlobalConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PositionDto(
		UUID positionId,
		@NotBlank @Pattern( regexp = GlobalConstants.PATTERN_NAME_FULL ) String positionName,
		@NotBlank @Pattern( regexp = GlobalConstants.PATTERN_NAME_FULL ) String positionDescription
		) {
	
}
