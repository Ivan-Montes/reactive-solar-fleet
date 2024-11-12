package dev.ime.api.endpoint;


import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;
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
import dev.ime.application.dto.PaginationDto;
import dev.ime.application.dto.ShipclassDto;
import dev.ime.application.utility.SortingValidator;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.config.UriConfigProperties;
import dev.ime.domain.port.inbound.QueryServicePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest({QueryEndpointHandler.class, QueryEndpointRouter.class})
class QueryEndpointHandlerTest {

    @MockBean
	private QueryServicePort<ShipclassDto> queryService;

    @MockBean
	private DtoValidator dtoValidator;

    @MockBean
	private SortingValidator sortingValidator;
    
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

    private final static String PATH = "/api/v1/shipclasses";

	private ShipclassDto shipclassDto0;
	private ShipclassDto shipclassDto1;

	private final UUID shipclassId0 = UUID.randomUUID();
	private final UUID shipclassId1 = UUID.randomUUID();
	private final String shipclassName = "";
	private final String shipclassDescription = "";
	
	@BeforeEach
	private void setUp() {
		
		shipclassDto0 = new ShipclassDto(
				shipclassId0,
				shipclassName,
				shipclassDescription);

		shipclassDto1 = new ShipclassDto(
				shipclassId1,
				shipclassName,
				shipclassDescription);			
		
	}	
    
	@Test
	void getAll_WithPageableInfo_ReturnEntities() {
		
	    ArgumentCaptor<PaginationDto> paginationDtoCaptor = ArgumentCaptor.forClass(PaginationDto.class);
		Mockito.when(sortingValidator.getDefaultSortField(Mockito.any(Class.class))).thenReturn(GlobalConstants.SHIPCLASS_ID);
		Mockito.when(dtoValidator.validateDto(paginationDtoCaptor.capture())).thenAnswer(invocation -> Mono.just(paginationDtoCaptor.getValue()));
		Mockito.when(queryService.getAll(Mockito.any(Pageable.class))).thenReturn(Flux.just(shipclassDto0, shipclassDto1));
		
		webTestClient
		.get().uri(uriBuilder -> uriBuilder
	            .path(PATH)
	            .queryParam("page", 0)
	            .queryParam("size", 10)
	            .queryParam("sortBy", GlobalConstants.PS_BY)
	            .queryParam("sortDir", GlobalConstants.PS_DIR)
	            .build())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
        .expectBodyList(ShipclassDto.class)
        .hasSize(2);
		Mockito.verify(sortingValidator).getDefaultSortField(Mockito.any(Class.class));
		Mockito.verify(dtoValidator).validateDto(Mockito.any(PaginationDto.class));
		Mockito.verify(queryService).getAll(Mockito.any(Pageable.class));
		
	}

	@Test
	void getAll_ThrowEx_ReturnResponseWithErrorInfo() {
		 
		Mockito.when(sortingValidator.getDefaultSortField(Mockito.any(Class.class))).thenThrow(new RuntimeException(GlobalConstants.EX_PLAIN));
		Mono<ServerResponse> serverResponse = ServerResponse
				.status(HttpStatus.I_AM_A_TEAPOT)
                .contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createErrorResponse());
		Mockito.when(errorHandler.handleException(Mockito.any(Throwable.class))).thenReturn(serverResponse);
		
		webTestClient
		.get().uri(PATH)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is4xxClientError()
		.expectBody(ErrorResponse.class)
		.value( response -> {
			org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(response).isNotNull(),
					() -> Assertions.assertThat(response.name()).isEqualTo(GlobalConstants.EX_ILLEGALARGUMENT)
					);
		});

		Mockito.verify(sortingValidator).getDefaultSortField(Mockito.any(Class.class));
		Mockito.verify(errorHandler).handleException(Mockito.any(Throwable.class));
		
	}

	@Test
	void getById_WithRightId_ReturnEntityFound() {
		
		Mockito.when(queryService.getById(Mockito.any(UUID.class))).thenReturn(Mono.just(shipclassDto0));
		
		webTestClient
		.get().uri(PATH + "/{id}", Collections.singletonMap("id", shipclassId0))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectBody(ShipclassDto.class)
		.value( dto -> {
			org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(dto).isNotNull(),
					() -> Assertions.assertThat(dto.shipclassId()).isEqualTo(shipclassId0)		
					);			
		});

		Mockito.verify(queryService).getById(Mockito.any(UUID.class));
		
	}

	@Test
	void getById_WithIdInvalid_ReturnResponseWithErrorInfo() {
		
		Mono<ServerResponse> serverResponse = ServerResponse
				.status(HttpStatus.I_AM_A_TEAPOT)
                .contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createErrorResponse());
		Mockito.when(errorHandler.handleException(Mockito.any(Throwable.class))).thenReturn(serverResponse);
		
		webTestClient
		.get().uri(PATH + "/{id}", Collections.singletonMap("id", "error-format"))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is4xxClientError()
		.expectBody(ErrorResponse.class)
		.value( response -> {
			org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(response).isNotNull(),
					() -> Assertions.assertThat(response.name()).isEqualTo(GlobalConstants.EX_ILLEGALARGUMENT)
					);
		});
		
		Mockito.verify(errorHandler).handleException(Mockito.any(Throwable.class));
	}
	
	private ErrorResponse createErrorResponse() {
		
		return new ErrorResponse(
        				UUID.randomUUID(),
        				GlobalConstants.EX_ILLEGALARGUMENT,
        				GlobalConstants.EX_ILLEGALARGUMENT_DESC,
        				Map.of("","")
            		);
	}
	
}