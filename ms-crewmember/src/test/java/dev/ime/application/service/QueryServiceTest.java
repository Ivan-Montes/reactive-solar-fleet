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
import dev.ime.application.dto.CrewMemberDto;
import dev.ime.config.CrewMemberMapper;
import dev.ime.domain.model.CrewMember;
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
	private CrewMemberMapper mapper;	

	@InjectMocks
	private QueryService queryService;

	private CrewMemberDto crewMemberDto0;
	private CrewMemberDto crewMemberDto1;
	private CrewMember crewMember0;
	private CrewMember crewMember1;
	
	private final UUID crewMemberId0 = UUID.randomUUID();
	private final UUID crewMemberId1 = UUID.randomUUID();
	private final String crewMemberName = "";
	private final String crewMemberSurname = "";
	private final UUID positionId = UUID.randomUUID();
	private final UUID spacecraftId = UUID.randomUUID();
	private final PageRequest pageRequest = PageRequest.of(0,100);
	
	@BeforeEach
	private void setUp() {

		crewMemberDto0 = new CrewMemberDto(
				crewMemberId0,
				crewMemberName,
				crewMemberSurname,
				positionId,
				spacecraftId);
		
		crewMemberDto1 = new CrewMemberDto(
				crewMemberId1,
				crewMemberName,
				crewMemberSurname,
				positionId,
				spacecraftId);
		
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
	
	@SuppressWarnings("unchecked")
	@Test
	void getAll_WithPageable_ReturnFluxEntities() {
		
		QueryHandler<Object> handler = Mockito.mock(QueryHandler.class);
		Mockito.when(queryDispatcher.getQueryHandler(Mockito.any(Query.class))).thenReturn(handler);
		Mockito.when(handler.handle(Mockito.any(Query.class))).thenReturn(Flux.just(crewMember0, crewMember1));
		Mockito.when(mapper.fromDomainToDto(Mockito.any(CrewMember.class))).thenReturn(crewMemberDto0).thenReturn(crewMemberDto1);
		
		StepVerifier
		.create(queryService.getAll(pageRequest))
		.expectNext(crewMemberDto0)
		.expectNext(crewMemberDto1)
		.verifyComplete();
		
		Mockito.verify(queryDispatcher).getQueryHandler(Mockito.any(Query.class));
		Mockito.verify(handler).handle(Mockito.any(Query.class));
		Mockito.verify(mapper, Mockito.times(2)).fromDomainToDto(Mockito.any(CrewMember.class));
	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void getById_WithID_ReturnDtoFound() {
		
		QueryHandler<Object> handler = Mockito.mock(QueryHandler.class);
		Mockito.when(queryDispatcher.getQueryHandler(Mockito.any(Query.class))).thenReturn(handler);
		Mockito.when(handler.handle(Mockito.any(Query.class))).thenReturn(Mono.just(crewMember0));
		Mockito.when(mapper.fromDomainToDto(Mockito.any(CrewMember.class))).thenReturn(crewMemberDto0);
		
		StepVerifier
		.create(queryService.getById(crewMemberId0))
		.expectNext(crewMemberDto0)
		.verifyComplete();
		
		Mockito.verify(queryDispatcher).getQueryHandler(Mockito.any(Query.class));
		Mockito.verify(handler).handle(Mockito.any(Query.class));
		Mockito.verify(mapper).fromDomainToDto(Mockito.any(CrewMember.class));
	
	}
	
}
