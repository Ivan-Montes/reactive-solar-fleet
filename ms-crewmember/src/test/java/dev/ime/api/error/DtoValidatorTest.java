package dev.ime.api.error;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import dev.ime.api.validation.DtoValidator;
import dev.ime.application.dto.PaginationDto;
import dev.ime.application.exception.ValidationException;
import dev.ime.config.GlobalConstants;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DtoValidatorTest {

	@Mock
	private Validator validator;

	@InjectMocks
	private DtoValidator dtoValidator;
	
	private final PaginationDto dto = new PaginationDto(0,1,"id","ASC");
	
	@Test
	void validateDto_WithRightDto_ReturnIt() {
		
		Mockito.doNothing().when(validator).validate(Mockito.any(PaginationDto.class), Mockito.any(Errors.class));
		
		Mono<PaginationDto> result = dtoValidator.validateDto(dto);
		
		StepVerifier
		.create(result)
		.expectNext(dto)
		.verifyComplete();
		Mockito.verify(validator).validate(Mockito.any(PaginationDto.class), Mockito.any(Errors.class));

	}

	@Test
	void validate_WithBadName_ThrowsValidationException() {		
		
		Mockito.doAnswer( exec -> {
			Errors errors = exec.getArgument(1);
			errors.rejectValue(GlobalConstants.PS_PAGE, GlobalConstants.MSG_UNKNOWDATA, GlobalConstants.EX_PLAIN_DESC);
			return null;
		}).when(validator).validate(Mockito.any(PaginationDto.class), Mockito.any(Errors.class));
		
		Mono<PaginationDto> result = dtoValidator.validateDto(dto);
		
		StepVerifier.create(result)
		.expectError(ValidationException.class)
		.verify();
		Mockito.verify(validator).validate(Mockito.any(PaginationDto.class), Mockito.any(Errors.class));
		
	}
	
}
