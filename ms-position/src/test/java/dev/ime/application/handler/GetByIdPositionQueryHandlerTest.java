package dev.ime.application.handler;


import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.usecase.GetByIdPositionQuery;
import dev.ime.domain.model.Position;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetByIdPositionQueryHandlerTest {

	@Mock
	private ReadRepositoryPort<Position> readRepository;

	@InjectMocks
	private GetByIdPositionQueryHandler getByIdPositionQueryHandler;	
	
	private GetByIdPositionQuery getByIdQuery;	
	private Position position;
	
	private final UUID positionId = UUID.randomUUID();
	private final String positionName = "";
	private final String positionDescription = "";

	@BeforeEach
	private void setUp() {	
		
		getByIdQuery = new GetByIdPositionQuery(positionId);
		
		position = new Position(
				positionId,
				positionName,
				positionDescription);
		
	}
		
	@Test
	void handle_WithId_ReturnMono() {
		
		Mockito.when(readRepository.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(position));
		
		Mono<Position> result = getByIdPositionQueryHandler.handle(getByIdQuery);
		
		StepVerifier.create(result)
		.expectNext(position)
		.verifyComplete();
		Mockito.verify(readRepository).findById(Mockito.any(UUID.class));
		
	}

}
