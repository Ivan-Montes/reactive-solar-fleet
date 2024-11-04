package dev.ime.api.error;


import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.application.exception.BasicException;
import dev.ime.application.exception.InvalidUUIDException;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

	@Mock
	private LoggerUtil loggerUtil;

	@InjectMocks
	private ErrorHandler errorHandler;
	
	@Test
	void handleException_WithBasicExceptionFamily_ReturnMonoServerResponseWithError() {

		BasicException ex = new InvalidUUIDException(Map.of(GlobalConstants.MSG_NODATA, "random-attack-uuid"));
	
		Mono<ServerResponse> result = errorHandler.handleException(ex);
		
		StepVerifier
		.create(result)
		.assertNext( response -> {
			org.junit.jupiter.api.Assertions.assertAll(
					()-> Assertions.assertThat(response).isNotNull(),
					()-> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.I_AM_A_TEAPOT)
					);
		})
		.verifyComplete();	
		
	}
	
	@Test
	void handleException_WithIllegalArgumentException_ReturnMonoServerResponseWithError() {
		
		IllegalArgumentException ex = new IllegalArgumentException(GlobalConstants.EX_PLAIN);
		
		Mono<ServerResponse> result = errorHandler.handleException(ex);
		
		StepVerifier.create(result)
        .assertNext(response -> {        	
        	org.junit.jupiter.api.Assertions.assertAll(
					()-> Assertions.assertThat(response).isNotNull(),
					()-> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
					);
        })
        .verifyComplete();		
	}	

	@Test
	void handleException_WithGenericException_ReturnMonoServerResponseWithError() {
		
		Exception ex = new Exception(GlobalConstants.EX_PLAIN);
		
		Mono<ServerResponse> result = errorHandler.handleException(ex);
		
		StepVerifier.create(result)		
		.assertNext(response -> {        	
        	org.junit.jupiter.api.Assertions.assertAll(
					()-> Assertions.assertThat(response).isNotNull(),
					()-> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
					);
        })	
		.verifyComplete();		
	}
	
}
