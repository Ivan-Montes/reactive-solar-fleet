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

import dev.ime.application.usecase.GetAllCrewMemberQuery;
import dev.ime.domain.model.CrewMember;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetAllCrewMemberQueryHandlerTest {

	@Mock
	private ReadRepositoryPort<CrewMember> readRepository;

	@InjectMocks
	private GetAllCrewMemberQueryHandler getAllCrewMemberQueryHandler;
	
	private CrewMember crewMember0;
	private CrewMember crewMember1;
	
	private final UUID crewMemberId0 = UUID.randomUUID();
	private final UUID crewMemberId1 = UUID.randomUUID();
	private final String crewMemberName = "";
	private final String crewMemberSurname = "";
	private final UUID positionId = UUID.randomUUID();
	private final UUID spacecraftId = UUID.randomUUID();
	private final PageRequest pageRequest = PageRequest.of(0,100);
	private final GetAllCrewMemberQuery getAllQuery = new GetAllCrewMemberQuery(pageRequest);
	
	@BeforeEach
	private void setUp() {
		
		crewMember0 = new CrewMember(
				crewMemberId0,
				crewMemberName,
				crewMemberSurname,
				positionId,
				spacecraftId);
		
		crewMember1 = new CrewMember(
				crewMemberId1,
				crewMemberName,
				crewMemberSurname,
				positionId,
				spacecraftId);
		
	}
	
	@Test
	void handle_WithQuery_ReturnFluxCrewMember() {
		
		Mockito.when(readRepository.findAll(Mockito.any(Pageable.class))).thenReturn(Flux.just(crewMember0, crewMember1));
		
		Flux<CrewMember> result = getAllCrewMemberQueryHandler.handle(getAllQuery);
		
		StepVerifier
		.create(result)
		.expectNext(crewMember0)
		.expectNext(crewMember1)
		.verifyComplete();
		Mockito.verify(readRepository).findAll(Mockito.any(Pageable.class));

	}

}
