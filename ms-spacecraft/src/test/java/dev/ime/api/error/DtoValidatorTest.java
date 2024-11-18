package dev.ime.api.error;


import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import dev.ime.api.validation.DtoValidator;
import dev.ime.application.dto.SpacecraftDto;
import dev.ime.application.exception.ValidationException;
import dev.ime.config.GlobalConstants;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DtoValidatorTest {

	@Spy
	private Validator validator;

	@InjectMocks
	private DtoValidator dtoValidator;
	
	private SpacecraftDto dto;

	private final UUID spacecraftId = UUID.randomUUID();
	private final String spacecraftName = "";
	private final UUID shipclassId = UUID.randomUUID();
	
	@BeforeEach
	private void setUp() {
		
		dto = new SpacecraftDto(
				spacecraftId,
				spacecraftName,
				shipclassId);
	}		

	@Test
	void validate_WithRighParam_ValidateOk() {
		
		Mockito.doNothing().when(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));
		
		Mono<SpacecraftDto> result = dtoValidator.validateDto(dto);
		
		StepVerifier.create(result)
		.expectNext(dto)
		.verifyComplete();
		Mockito.verify(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));
		
	}	
	
	@Test
	void validate_WithBadName_ThrowsValidationException() {
		
		Mockito.doAnswer( exec -> {
			Errors errors = exec.getArgument(1);
			errors.rejectValue(GlobalConstants.SPACECRAFT_ID, GlobalConstants.MSG_UNKNOWDATA, GlobalConstants.EX_PLAIN_DESC);
			return null;
		}).when(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));
		
		Mono<SpacecraftDto> result = dtoValidator.validateDto(dto);
		
		StepVerifier.create(result)
		.expectError(ValidationException.class)
		.verify();
		Mockito.verify(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));
		
	}
	

}
