package dev.ime.application.handler;


import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.usecase.GetByIdCrewMemberQuery;
import dev.ime.domain.model.CrewMember;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetByIdCrewMemberQueryHandlerTest {

	@Mock
	private ReadRepositoryPort<CrewMember> readRepository;

	@InjectMocks
	private GetByIdCrewMemberQueryHandler getByIdCrewMemberQueryHandler;

	private CrewMember crewMember;
	
	private final UUID crewMemberId = UUID.randomUUID();
	private final String crewMemberName = "";
	private final String crewMemberSurname = "";
	private final UUID positionId = UUID.randomUUID();
	private final UUID spacecraftId = UUID.randomUUID();
	private final GetByIdCrewMemberQuery getByIdCrewMemberQuery = new GetByIdCrewMemberQuery(crewMemberId);

	@BeforeEach
	private void setUp() {
		
		crewMember = new CrewMember(
				crewMemberId,
				crewMemberName,
				crewMemberSurname,
				positionId,
				spacecraftId);
		
	}
	
	@Test
	void handle_WithQueryOk_ReturnCrewMemberFound() {
		
		Mockito.when(readRepository.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(crewMember));
		
		Mono<CrewMember> result = getByIdCrewMemberQueryHandler.handle(getByIdCrewMemberQuery);
		
		StepVerifier
		.create(result)
		.expectNext(crewMember)
		.verifyComplete();
		Mockito.verify(readRepository).findById(Mockito.any(UUID.class));

	}

}
