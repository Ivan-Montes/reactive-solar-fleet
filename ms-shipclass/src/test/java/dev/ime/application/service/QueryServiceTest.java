package dev.ime.application.service;


import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import dev.ime.application.dispatcher.QueryDispatcher;
import dev.ime.application.dto.ShipclassDto;
import dev.ime.config.ShipclassMapper;
import dev.ime.domain.model.Shipclass;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class QueryServiceTest {

	@Mock
	private QueryDispatcher queryDispatcher;

	@Mock
	private ShipclassMapper mapper;	
	
	@InjectMocks
	private QueryService queryService;

	private Shipclass shipclass0;
	private Shipclass shipclass1;
	private ShipclassDto shipclassDto0;
	private ShipclassDto shipclassDto1;

	private final UUID shipclassId0 = UUID.randomUUID();
	private final UUID shipclassId1 = UUID.randomUUID();
	private final String shipclassName = "";
	private final String shipclassDescription = "";
	private final PageRequest pageRequest = PageRequest.of(0, 100);

	@BeforeEach
	private void setUp() {				
		
		shipclass0 = new Shipclass(
				shipclassId0,
				shipclassName,
				shipclassDescription);
		
		shipclass1 = new Shipclass();
		shipclass1.setShipclassId(shipclassId1);
		shipclass1.setShipclassName(shipclassName);
		shipclass1.setShipclassDescription(shipclassDescription);
		
		shipclassDto0 = new ShipclassDto(
				shipclassId0,
				shipclassName,
				shipclassDescription);

		shipclassDto1 = new ShipclassDto(
				shipclassId1,
				shipclassName,
				shipclassDescription);			
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void getAll_ByDefault_ReturnFluxMultiple() {
		
		QueryHandler<Object> handler = Mockito.mock(QueryHandler.class);
		Mockito.when(queryDispatcher.getQueryHandler(Mockito.any(Query.class))).thenReturn(handler);
		Mockito.when(handler.handle(Mockito.any(Query.class))).thenReturn(Flux.just(shipclass0, shipclass1));
		Mockito.when(mapper.fromDomainToDto(Mockito.any(Shipclass.class))).thenReturn(shipclassDto0).thenReturn(shipclassDto1);
		
		Flux<ShipclassDto> result = queryService.getAll(pageRequest);
		
		StepVerifier.create(result)
		.expectNext(shipclassDto0)  
		.expectNext(shipclassDto1)  
		.verifyComplete();
		Mockito.verify(queryDispatcher).getQueryHandler(Mockito.any(Query.class));
		Mockito.verify(handler).handle(Mockito.any(Query.class));
		Mockito.verify(mapper, Mockito.times(2)).fromDomainToDto(Mockito.any(Shipclass.class));

	}

	@SuppressWarnings("unchecked")
	@Test
	void getById_WithRighParam_ReturnMonoElement() {
		
		QueryHandler<Object> handler = Mockito.mock(QueryHandler.class);
		Mockito.when(queryDispatcher.getQueryHandler(Mockito.any(Query.class))).thenReturn(handler);
		Mockito.when(handler.handle(Mockito.any(Query.class))).thenReturn(Mono.just(shipclass0));
		Mockito.when(mapper.fromDomainToDto(Mockito.any(Shipclass.class))).thenReturn(shipclassDto0);

		Mono<ShipclassDto> result = queryService.getById(shipclassId0);
		
		StepVerifier.create(result)
		.expectNext(shipclassDto0)  
		.verifyComplete();
		Mockito.verify(queryDispatcher).getQueryHandler(Mockito.any(Query.class));
		Mockito.verify(handler).handle(Mockito.any(Query.class));
		Mockito.verify(mapper).fromDomainToDto(Mockito.any(Shipclass.class));

	}

}
