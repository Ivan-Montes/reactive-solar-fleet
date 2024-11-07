package dev.ime.infrastructure.adapter;


import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import dev.ime.config.CrewMemberMapper;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.model.CrewMember;
import dev.ime.infrastructure.entity.CrewMemberJpaEntity;
import dev.ime.infrastructure.repository.CrewMemberReadRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CrewMemberReadRepositoryAdapterTest {

	@Mock
	private CrewMemberReadRepository crewMemberRepository;
	
	@Mock
	private CrewMemberMapper mapper;

	@InjectMocks
	private CrewMemberReadRepositoryAdapter crewMemberReadRepositoryAdapter;	

	private CrewMemberJpaEntity crewMemberJpaEntity0;
	private CrewMemberJpaEntity crewMemberJpaEntity1;
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

		crewMemberJpaEntity0 = new CrewMemberJpaEntity(
				crewMemberId0,
				crewMemberName,
				crewMemberSurname,
				positionId,
				spacecraftId);
		
		crewMemberJpaEntity1 = new CrewMemberJpaEntity();
		crewMemberJpaEntity1.setCrewMemberId(crewMemberId0);
		crewMemberJpaEntity1.setCrewMemberName(crewMemberName);
		crewMemberJpaEntity1.setCrewMemberSurname(crewMemberSurname);
		crewMemberJpaEntity1.setPositionId(positionId);
		crewMemberJpaEntity1.setSpacecraftId(spacecraftId);
		
		crewMember0 = new CrewMember(
				crewMemberId0,
				crewMemberName,
				crewMemberSurname,
				positionId,
				spacecraftId);
		
		crewMember1 = new CrewMember();
		crewMember1.setCrewMemberId(crewMemberId1);
		crewMember1.setCrewMemberName(crewMemberName);
		crewMember1.setCrewMemberSurname(crewMemberSurname);
		crewMember1.setPositionId(positionId);
		crewMember1.setSpacecraftId(spacecraftId);
		
	}
	
	@Test
	void findAll_ByDefault_ReturnFluxMultiple() {

		Mockito.when(crewMemberRepository.findAllBy(Mockito.any(Pageable.class))).thenReturn(Flux.just(crewMemberJpaEntity0, crewMemberJpaEntity1));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(CrewMemberJpaEntity.class))).thenReturn(crewMember0).thenReturn(crewMember1);
		
		Flux<CrewMember> result = crewMemberReadRepositoryAdapter.findAll(pageRequest);
		
		StepVerifier
		.create(result)
		.expectNext(crewMember0)
		.expectNext(crewMember1)
		.verifyComplete();
		Mockito.verify(crewMemberRepository).findAllBy(Mockito.any(Pageable.class));
		Mockito.verify(mapper, Mockito.times(2)).fromJpaToDomain(Mockito.any(CrewMemberJpaEntity.class));
		
	}

	@Test
	void findAll_WhenRepositoryThrowsError_PropagateException() {
		
		RuntimeException ex = new RuntimeException(GlobalConstants.EX_PLAIN);
		Mockito.when(crewMemberRepository.findAllBy(Mockito.any(Pageable.class))).thenReturn(Flux.error(ex));

		Flux<CrewMember> result = crewMemberReadRepositoryAdapter.findAll(pageRequest);
		
		StepVerifier.create(result)
		.expectError(RuntimeException.class)
		.verify();		
		Mockito.verify(crewMemberRepository).findAllBy(Mockito.any(Pageable.class));

	}

	@Test
	void findById_WithValidId_ReturnMonoPosition() {
		
		Mockito.when(crewMemberRepository.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(crewMemberJpaEntity0));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(CrewMemberJpaEntity.class))).thenReturn(crewMember0);

		Mono<CrewMember> result = crewMemberReadRepositoryAdapter.findById(crewMemberId0);
		
		StepVerifier
		.create(result)
		.assertNext( entityFound -> {
			org.junit.jupiter.api.Assertions.assertAll(
					()->Assertions.assertThat(entityFound).isEqualTo(crewMember0),
					()->Assertions.assertThat(entityFound).isNotEqualTo(crewMember1),
        			()->Assertions.assertThat(entityFound).hasSameHashCodeAs(crewMember0)
        			);
		})		.verifyComplete();		
		Mockito.verify(crewMemberRepository).findById(Mockito.any(UUID.class));
		Mockito.verify(mapper).fromJpaToDomain(Mockito.any(CrewMemberJpaEntity.class));
		
	}

	@Test
	void findByNameAndSurname_WithRightParameters_ReturnEntities() {
		
		Mockito.when(crewMemberRepository.findByCrewMemberNameAndCrewMemberSurname(Mockito.anyString(), Mockito.anyString())).thenReturn(Flux.just(crewMemberJpaEntity0));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(CrewMemberJpaEntity.class))).thenReturn(crewMember0);

		Flux<CrewMember> result = crewMemberReadRepositoryAdapter.findByNameAndSurname(crewMemberName, crewMemberSurname);
		
		StepVerifier
		.create(result)
		.expectNext(crewMember0)
		.verifyComplete();	
		Mockito.verify(crewMemberRepository).findByCrewMemberNameAndCrewMemberSurname(Mockito.anyString(), Mockito.anyString());
		Mockito.verify(mapper).fromJpaToDomain(Mockito.any(CrewMemberJpaEntity.class));

	}	

}
