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
import dev.ime.application.dto.SpacecraftDto;
import dev.ime.config.SpacecraftMapper;
import dev.ime.domain.model.Spacecraft;
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
	private SpacecraftMapper mapper;
	
	@InjectMocks
	private QueryService queryService;

	private Spacecraft spacecraft0;
	private Spacecraft spacecraft1;
	private SpacecraftDto spacecraftDto0;
	private SpacecraftDto spacecraftDto1;

	private final UUID spacecraftId0= UUID.randomUUID();
	private final UUID spacecraftId1 = UUID.randomUUID();
	private final String spacecraftName = "";
	private final UUID shipclassId = UUID.randomUUID();
	private final PageRequest pageRequest = PageRequest.of(0, 100);
	
	@BeforeEach
	private void setUp() {				
		
		spacecraft0 = new Spacecraft(
				spacecraftId0,
				spacecraftName,
				shipclassId);
		
		spacecraft1 = new Spacecraft();
		spacecraft1.setSpacecraftId(spacecraftId1);
		spacecraft1.setSpacecraftName(spacecraftName);
		spacecraft1.setSpacecraftId(shipclassId);

		spacecraftDto0 = new SpacecraftDto(
				spacecraftId0,
				spacecraftName,
				shipclassId);

		spacecraftDto1 = new SpacecraftDto(
				spacecraftId1,
				spacecraftName,
				shipclassId);
	}
	

	@SuppressWarnings("unchecked")
	@Test
	void getAll_ByDefault_ReturnFluxMultiple() {
		
		QueryHandler<Object> handler = Mockito.mock(QueryHandler.class);
		Mockito.when(queryDispatcher.getQueryHandler(Mockito.any(Query.class))).thenReturn(handler);
		Mockito.when(handler.handle(Mockito.any(Query.class))).thenReturn(Flux.just(spacecraft0, spacecraft1));
		Mockito.when(mapper.fromDomainToDto(Mockito.any(Spacecraft.class))).thenReturn(spacecraftDto0).thenReturn(spacecraftDto1);
		
		Flux<SpacecraftDto> result = queryService.getAll(pageRequest);
		
		StepVerifier.create(result)
		.expectNext(spacecraftDto0)  
		.expectNext(spacecraftDto1)  
		.verifyComplete();
		Mockito.verify(queryDispatcher).getQueryHandler(Mockito.any(Query.class));
		Mockito.verify(handler).handle(Mockito.any(Query.class));
		Mockito.verify(mapper, Mockito.times(2)).fromDomainToDto(Mockito.any(Spacecraft.class));

	}

	@SuppressWarnings("unchecked")
	@Test
	void getById_WithRighParam_ReturnMonoElement() {
		
		QueryHandler<Object> handler = Mockito.mock(QueryHandler.class);
		Mockito.when(queryDispatcher.getQueryHandler(Mockito.any(Query.class))).thenReturn(handler);
		Mockito.when(handler.handle(Mockito.any(Query.class))).thenReturn(Mono.just(spacecraft0));
		Mockito.when(mapper.fromDomainToDto(Mockito.any(Spacecraft.class))).thenReturn(spacecraftDto0);

		Mono<SpacecraftDto> result = queryService.getById(spacecraftId0);
		
		StepVerifier.create(result)
		.expectNext(spacecraftDto0)  
		.verifyComplete();
		Mockito.verify(queryDispatcher).getQueryHandler(Mockito.any(Query.class));
		Mockito.verify(handler).handle(Mockito.any(Query.class));
		Mockito.verify(mapper).fromDomainToDto(Mockito.any(Spacecraft.class));

	}

}
