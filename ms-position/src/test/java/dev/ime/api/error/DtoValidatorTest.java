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
import dev.ime.application.dto.PositionDto;
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
	
	private PositionDto positionDto;
	private final UUID positionId = UUID.randomUUID();
	private final String positionName = "";
	private final String positionDescription = "";
	

	@BeforeEach
	private void setUp() {

		positionDto = new PositionDto(
				positionId,
				positionName,
				positionDescription);
		
	}
		
		
	@Test
	void validate_WithRighPosition_ValidateOk() {
		
		Mockito.doNothing().when(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));
		
		Mono<PositionDto> result = dtoValidator.validateDto(positionDto);
		
		StepVerifier.create(result)
		.expectNext(positionDto)
		.verifyComplete();
		Mockito.verify(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));
		
	}
	
	
	@Test
	void validate_WithBadName_ThrowsValidationException() {
		
		Mockito.doAnswer( exec -> {
			Errors errors = exec.getArgument(1);
			errors.rejectValue(GlobalConstants.POSITION_ID, GlobalConstants.MSG_UNKNOWDATA, GlobalConstants.EX_PLAIN_DESC);
			return null;
		}).when(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));
		
		Mono<PositionDto> result = dtoValidator.validateDto(positionDto);
		
		StepVerifier.create(result)
		.expectError(ValidationException.class)
		.verify();
		Mockito.verify(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));
		
	}
	

}
