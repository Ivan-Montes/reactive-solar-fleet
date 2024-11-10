package dev.ime.api.endpoint;


import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.api.error.ErrorHandler;
import dev.ime.api.validation.DtoValidator;
import dev.ime.application.dto.ErrorResponse;
import dev.ime.application.dto.PositionDto;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.config.UriConfigProperties;
import dev.ime.domain.event.Event;
import dev.ime.domain.port.inbound.CommandServicePort;
import reactor.core.publisher.Mono;

@WebFluxTest({CommandEndpointHandler.class, CommandEndpointRouter.class})
class CommandEndpointHandlerTest {

    @MockBean
	private CommandServicePort<PositionDto> commandService;

    @MockBean
	private DtoValidator dtoValidator;

    @MockBean
	private ErrorHandler errorHandler;	
	
    @Autowired
    private WebTestClient webTestClient;    

    @MockBean
    private LoggerUtil loggerUtil;
    
	@TestConfiguration
	static class TestConfig {		
	    @Bean
	    UriConfigProperties uriConfigProperties() {
	        UriConfigProperties props = new UriConfigProperties();
	        props.setEndpointUri(PATH);
	        return props;
	    }	    
	    @Bean
	    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
	        return http	
	                   .authorizeExchange(exchanges -> exchanges
	                           .anyExchange().permitAll()
	                		   )
		        		.csrf(CsrfSpec::disable)	
	                   .build();
	    }
	}

    private final static String PATH = "/api/v1/positions";
	private Event event;
	private PositionDto positionDto;
	
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.POSITION_CAT;
	private final String eventType = GlobalConstants.POSITION_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();
	
	private final UUID positionId = UUID.randomUUID();
	private final String positionName = "";
	private final String positionDescription = "";
	
	@BeforeEach
	private void setUp() {

		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
		positionDto = new PositionDto(
				positionId,
				positionName,
				positionDescription);
		
	}
	
	@Test
	void create_WithValidEntity_ReturnsCreatedEventWithOkStatus() {
		
		Mockito.when(dtoValidator.validateDto(Mockito.any(PositionDto.class))).thenReturn(Mono.just(positionDto));
		Mockito.when(commandService.create(Mockito.any(PositionDto.class))).thenReturn(Mono.just(event));
	
	    webTestClient
        .post().uri(PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(positionDto), PositionDto.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Event.class)
        .value(result -> {
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getEventId()).isEqualTo(event.getEventId());
            Assertions.assertThat(result.getEventCategory()).isEqualTo(event.getEventCategory());
        });

    Mockito.verify(dtoValidator).validateDto(Mockito.any(PositionDto.class));
    Mockito.verify(commandService).create(Mockito.any(PositionDto.class));
	
	}
	

	@Test
	void update_WithValidEntity_ReturnsUpdatedEventWithOkStatus() {
		
		Mockito.when(dtoValidator.validateDto(Mockito.any(PositionDto.class))).thenReturn(Mono.just(positionDto));
		Mockito.when(commandService.update( Mockito.any(UUID.class), Mockito.any(PositionDto.class))).thenReturn(Mono.just(event));

		webTestClient
		.put().uri(PATH + "/{id}", Collections.singletonMap("id", positionId))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(positionDto), PositionDto.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Event.class)
        .value(result -> {
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getEventId()).isEqualTo(event.getEventId());
            Assertions.assertThat(result.getEventCategory()).isEqualTo(event.getEventCategory());
        });

	    Mockito.verify(dtoValidator).validateDto(Mockito.any(PositionDto.class));
	    Mockito.verify(commandService).update(Mockito.any(UUID.class), Mockito.any(PositionDto.class));
	}

	@Test
	void update_WithInvalidId_ReturnsServerResponseError() {
		
		Mono<ServerResponse> serverResponse = ServerResponse
                .status(HttpStatus.I_AM_A_TEAPOT)
                .contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createErrorResponse());
                
		Mockito.when(errorHandler.handleException(Mockito.any(Throwable.class))).thenReturn(serverResponse);
		
		webTestClient
		.put().uri(PATH + "/{id}", Collections.singletonMap("id", "uuid-bad-format"))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(positionDto), PositionDto.class)
        .exchange()
        .expectStatus().is4xxClientError()
		.expectBody(ErrorResponse.class)
		.value( response -> {
			org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(response).isNotNull(),
					() -> Assertions.assertThat(response.name()).isEqualTo(GlobalConstants.EX_ILLEGALARGUMENT)
					);
		});
	}

	private ErrorResponse createErrorResponse() {
		
		return new ErrorResponse(
        				UUID.randomUUID(),
        				GlobalConstants.EX_ILLEGALARGUMENT,
        				GlobalConstants.EX_ILLEGALARGUMENT_DESC,
        				Map.of("","")
            		);
	}

	@Test
	void deleteById_WithValidId_ReturnsDeletedEventWithOkStatus() {
		
		Mockito.when(commandService.deleteById( Mockito.any(UUID.class))).thenReturn(Mono.just(event));

		webTestClient
		.delete().uri(PATH + "/{id}", Collections.singletonMap("id", positionId))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Event.class)
        .value(result -> {
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getEventId()).isEqualTo(event.getEventId());
            Assertions.assertThat(result.getEventCategory()).isEqualTo(event.getEventCategory());
        });
		
		Mockito.verify(commandService).deleteById( Mockito.any(UUID.class));

	}
	
}
