package dev.ime.application.handler;


import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import dev.ime.application.usecase.GetAllPositionQuery;
import dev.ime.domain.model.Position;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetAllPositionQueryHandlerTest {

	@Mock
	private ReadRepositoryPort<Position> readRepository;

	@InjectMocks
	private GetAllPositionQueryHandler getAllPositionQueryHandler;

	private GetAllPositionQuery getAllQuery;

	private Position position0;
	private Position position1;
	private final UUID positionId0 = UUID.randomUUID();
	private final UUID positionId1 = UUID.randomUUID();
	private final String positionName = "";
	private final String positionDescription = "";
	
	@BeforeEach
	private void setUp() {
		
		getAllQuery = new GetAllPositionQuery(PageRequest.of(0,10));
		
		position0 = new Position(
				positionId0,
				positionName,
				positionDescription);
		
		position1 = new Position(
				positionId1,
				positionName,
				positionDescription);
	}
	
	@Test
	void handle_ByDefault_ReturnFluxMultiple() {
		
		Mockito.when(readRepository.findAll(Mockito.any(Pageable.class))).thenReturn(Flux.just(position0,position1));
		
		Flux<Position> result =	getAllPositionQueryHandler.handle(getAllQuery);
		
		StepVerifier.create(result)
		.expectNext(position0)
		.expectNext(position1)
		.verifyComplete();
		Mockito.verify(readRepository).findAll(Mockito.any(Pageable.class));
		
	}

}
