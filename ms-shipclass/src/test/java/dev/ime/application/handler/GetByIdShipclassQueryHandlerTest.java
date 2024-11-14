package dev.ime.application.handler;


import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.usecase.GetByIdShipclassQuery;
import dev.ime.domain.model.Shipclass;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetByIdShipclassQueryHandlerTest {

	@Mock
	private ReadRepositoryPort<Shipclass> readRepository;

	@InjectMocks
	private GetByIdShipclassQueryHandler getByIdShipclassQueryHandler;
	
	private GetByIdShipclassQuery getByIdQuery;
	private Shipclass shipclass;

	private final UUID shipclassId = UUID.randomUUID();
	private final String shipclassName = "";
	private final String shipclassDescription = "";

	@BeforeEach
	private void setUp() {

		getByIdQuery = new GetByIdShipclassQuery(shipclassId);
		
		shipclass = new Shipclass(
				shipclassId,
				shipclassName,
				shipclassDescription);
		
	}
	
	@Test
	void handle_WithId_ReturnMono() {
		
		Mockito.when(readRepository.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(shipclass));
		
		Mono<Shipclass> result = getByIdShipclassQueryHandler.handle(getByIdQuery);
		
		StepVerifier.create(result)
		.expectNext(shipclass)
		.verifyComplete();
		Mockito.verify(readRepository).findById(Mockito.any(UUID.class));
		
	}

}
