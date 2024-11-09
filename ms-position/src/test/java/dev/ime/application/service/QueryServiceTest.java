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
import dev.ime.application.dto.PositionDto;
import dev.ime.config.PositionMapper;
import dev.ime.domain.model.Position;
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
	private PositionMapper mapper;
    
	@InjectMocks
	private QueryService queryService;

	private Position position0;
	private Position position1;
	private PositionDto positionDto0;
	private PositionDto positionDto1;
	
	private final UUID positionId0 = UUID.randomUUID();
	private final UUID positionId1 = UUID.randomUUID();
	private final String positionName = "";
	private final String positionDescription = "";
	private final PageRequest pageRequest = PageRequest.of(0,99);
	
	@BeforeEach
	private void setUp() {

		position0 = new Position(
				positionId0,
				positionName,
				positionDescription);
		
		position1 = new Position(
				positionId1,
				positionName,
				positionDescription);
		
		positionDto0 = new PositionDto(
				positionId0,
				positionName,
				positionDescription);
		
		positionDto1 = new PositionDto(
				positionId1,
				positionName,
				positionDescription);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void getAll_ByDefault_ReturnFluxMultiple() {
		
		QueryHandler<Object> handler = Mockito.mock(QueryHandler.class);
		Mockito.when(queryDispatcher.getQueryHandler(Mockito.any(Query.class))).thenReturn(handler);
		Mockito.when(handler.handle(Mockito.any(Query.class))).thenReturn(Flux.just(position0, position1));
		Mockito.when(mapper.fromDomainToDto(Mockito.any(Position.class))).thenReturn(positionDto0).thenReturn(positionDto1);
		
		Flux<PositionDto> result = queryService.getAll(pageRequest);
		
		StepVerifier.create(result)
		.expectNext(positionDto0)  
		.expectNext(positionDto1)  
		.verifyComplete();
		Mockito.verify(queryDispatcher).getQueryHandler(Mockito.any(Query.class));
		Mockito.verify(handler).handle(Mockito.any(Query.class));
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void getById_WithRighParam_ReturnMonoElement() {
		
		QueryHandler<Object> handler = Mockito.mock(QueryHandler.class);
		Mockito.when(queryDispatcher.getQueryHandler(Mockito.any(Query.class))).thenReturn(handler);
		Mockito.when(handler.handle(Mockito.any(Query.class))).thenReturn(Mono.just(position0));
		Mockito.when(mapper.fromDomainToDto(Mockito.any(Position.class))).thenReturn(positionDto0);

		Mono<PositionDto> result = queryService.getById(positionId0);
		
		StepVerifier.create(result)
		.expectNext(positionDto0)  
		.verifyComplete();
		Mockito.verify(queryDispatcher).getQueryHandler(Mockito.any(Query.class));
		Mockito.verify(handler).handle(Mockito.any(Query.class));
		
	}
	
}
