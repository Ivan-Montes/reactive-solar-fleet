package dev.ime.application.dto;

import java.util.UUID;

import dev.ime.config.GlobalConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SpacecraftDto(
		UUID spacecraftId,
		@NotBlank @Pattern(regexp = GlobalConstants.PATTERN_NAME_FULL) String spacecraftName,
		@NotNull UUID shipclassId	
		) {

}
