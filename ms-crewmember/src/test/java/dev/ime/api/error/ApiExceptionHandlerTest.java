package dev.ime.api.error;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ApiExceptionHandlerTest {

	@Mock
    private LoggerUtil loggerUtil;

	@InjectMocks
	private ApiExceptionHandler apiExceptionHandler;	
	
	@Test
	void handleNoResourceFoundException_shouldReturnErrorInfo() {
		
		NoResourceFoundException ex = new NoResourceFoundException(GlobalConstants.EX_PLAIN);

		StepVerifier
		.create(apiExceptionHandler.handleNoResourceFoundException(ex))
		.assertNext( response -> {
			org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(response).isNotNull(),
					() -> Assertions.assertThat(response.getBody().name()).isEqualTo(GlobalConstants.EX_RESOURCENOTFOUND)
					);
		})
		.verifyComplete();
		
	}
	
	@Test
	void createHandleGenericExceptionErrorResponse_shouldReturnErrorInfo() {
		
		Exception ex = new Exception(GlobalConstants.MSG_EVENT_ILLEGAL);

		StepVerifier
		.create(apiExceptionHandler.handleGenericException(ex))
		.assertNext( response -> {
			org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(response).isNotNull(),
					() -> Assertions.assertThat(response.getBody().name()).isEqualTo(GlobalConstants.EX_PLAIN)
					);
		})
		.verifyComplete();
		
	}	

}
