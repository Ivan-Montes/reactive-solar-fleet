package dev.ime.application.dto;

import java.util.UUID;

import dev.ime.config.GlobalConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ShipclassDto(
		UUID shipclassId,
		@NotBlank @Pattern( regexp = GlobalConstants.PATTERN_NAME_FULL ) String shipclassName,
		@NotBlank @Pattern( regexp = GlobalConstants.PATTERN_NAME_FULL ) String shipclassDescription
		) {
	
}
