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

import dev.ime.application.usecase.GetAllShipclassQuery;
import dev.ime.domain.model.Shipclass;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetAllShipclassQueryHandlerTest {

	@Mock
	private ReadRepositoryPort<Shipclass> readRepository;

	@InjectMocks
	private GetAllShipclassQueryHandler getAllShipclassQueryHandler;
	
	private GetAllShipclassQuery getAllQuery;
	private Shipclass shipclass0;
	private Shipclass shipclass1;

	private final UUID shipclassId0 = UUID.randomUUID();
	private final UUID shipclassId1 = UUID.randomUUID();
	private final String shipclassName = "";
	private final String shipclassDescription = "";
	private final PageRequest pageRequest = PageRequest.of(0, 100);

	@BeforeEach
	private void setUp() {

		getAllQuery = new GetAllShipclassQuery(pageRequest);
		
		shipclass0 = new Shipclass(
				shipclassId0,
				shipclassName,
				shipclassDescription);

		shipclass1 = new Shipclass(
				shipclassId1,
				shipclassName,
				shipclassDescription);	
		
	}

	@Test
	void handle_ByDefault_ReturnFluxMultiple() {
		
		Mockito.when(readRepository.findAll(Mockito.any(Pageable.class))).thenReturn(Flux.just(shipclass0,shipclass1));
		
		Flux<Shipclass> result =	getAllShipclassQueryHandler.handle(getAllQuery);
		
		StepVerifier.create(result)
		.expectNext(shipclass0)
		.expectNext(shipclass1)
		.verifyComplete();
		Mockito.verify(readRepository).findAll(Mockito.any(Pageable.class));
		
	}

}
