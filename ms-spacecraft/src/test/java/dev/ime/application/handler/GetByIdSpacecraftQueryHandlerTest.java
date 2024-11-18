package dev.ime.application.handler;


import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.usecase.GetByIdSpacecraftQuery;
import dev.ime.domain.model.Spacecraft;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetByIdSpacecraftQueryHandlerTest {

	@Mock
	private ReadRepositoryPort<Spacecraft> readRepository;

	@InjectMocks
	private GetByIdSpacecraftQueryHandler getByIdSpacecraftQueryHandler;

	private GetByIdSpacecraftQuery getByIdQuery;
	private Spacecraft spacecraft;
	
	private final UUID spacecraftId = UUID.randomUUID();
	private final String spacecraftName = "";
	private final UUID shipclassId = UUID.randomUUID();

	@BeforeEach
	private void setUp() {				
		
		getByIdQuery = new GetByIdSpacecraftQuery(spacecraftId);
		
		spacecraft = new Spacecraft(
				spacecraftId,
				spacecraftName,
				shipclassId);		
		
	}

	@Test
	void handle_WithId_ReturnMono() {
		
		Mockito.when(readRepository.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(spacecraft));
		
		Mono<Spacecraft> result = getByIdSpacecraftQueryHandler.handle(getByIdQuery);
		
		StepVerifier.create(result)
		.expectNext(spacecraft)
		.verifyComplete();
		Mockito.verify(readRepository).findById(Mockito.any(UUID.class));
		
	}

}
