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

import dev.ime.application.usecase.GetAllSpacecraftQuery;
import dev.ime.domain.model.Spacecraft;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetAllSpacecraftQueryHandlerTest {

	@Mock
	private ReadRepositoryPort<Spacecraft> readRepository;

	@InjectMocks
	private GetAllSpacecraftQueryHandler getAllSpacecraftQueryHandler;
	
	private GetAllSpacecraftQuery getAllQuery;
	private Spacecraft spacecraft0;
	private Spacecraft spacecraft1;

	private final UUID spacecraftId0= UUID.randomUUID();
	private final UUID spacecraftId1 = UUID.randomUUID();
	private final String spacecraftName = "";
	private final UUID shipclassId = UUID.randomUUID();
	private final PageRequest pageRequest = PageRequest.of(0, 100);
	
	@BeforeEach
	private void setUp() {				
		
		getAllQuery = new GetAllSpacecraftQuery(pageRequest);
		
		spacecraft0 = new Spacecraft(
				spacecraftId0,
				spacecraftName,
				shipclassId);
		
		spacecraft1 = new Spacecraft();
		spacecraft1.setSpacecraftId(spacecraftId1);
		spacecraft1.setSpacecraftName(spacecraftName);
		spacecraft1.setSpacecraftId(shipclassId);
		
	}

	@Test
	void handle_ByDefault_ReturnFluxMultiple() {
		
		Mockito.when(readRepository.findAll(Mockito.any(Pageable.class))).thenReturn(Flux.just(spacecraft0,spacecraft1));
		
		Flux<Spacecraft> result =	getAllSpacecraftQueryHandler.handle(getAllQuery);
		
		StepVerifier.create(result)
		.expectNext(spacecraft0)
		.expectNext(spacecraft1)
		.verifyComplete();
		Mockito.verify(readRepository).findAll(Mockito.any(Pageable.class));
		
	}


}
