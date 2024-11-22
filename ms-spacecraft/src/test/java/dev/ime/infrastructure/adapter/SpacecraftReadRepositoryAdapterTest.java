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

import dev.ime.config.GlobalConstants;
import dev.ime.config.SpacecraftMapper;
import dev.ime.domain.model.Spacecraft;
import dev.ime.infrastructure.entity.SpacecraftJpaEntity;
import dev.ime.infrastructure.repository.SpacecraftReadRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SpacecraftReadRepositoryAdapterTest {

	@Mock
	private SpacecraftReadRepository spacecraftReadRepository;

	@Mock
	private SpacecraftMapper mapper;

	@InjectMocks
	private SpacecraftReadRepositoryAdapter spacecraftReadRepositoryAdapter;	

	private SpacecraftJpaEntity spacecraftJpaEntity0;
	private Spacecraft spacecraft0;
	private SpacecraftJpaEntity spacecraftJpaEntity1;
	private Spacecraft spacecraft1;

	private final UUID spacecraftId0 = UUID.randomUUID();
	private final UUID spacecraftId1 = UUID.randomUUID();
	private final String spacecraftName = "";
	private final UUID shipclassId = UUID.randomUUID();
	private final PageRequest pageRequest = PageRequest.of(0, 100);

	@BeforeEach
	private void setUp() {
		
		spacecraftJpaEntity0 = new SpacecraftJpaEntity(
				spacecraftId0,
				spacecraftName,
				shipclassId);		
		
		spacecraft0 = new Spacecraft(
				spacecraftId0,
				spacecraftName,
				shipclassId);

		spacecraftJpaEntity1 = new SpacecraftJpaEntity();
		spacecraftJpaEntity1.setSpacecraftId(spacecraftId1);
		spacecraftJpaEntity1.setSpacecraftName(spacecraftName);
		spacecraftJpaEntity1.setSpacecraftId(shipclassId);
		
		spacecraft1 = new Spacecraft();
		spacecraft1.setSpacecraftId(spacecraftId1);
		spacecraft1.setSpacecraftName(spacecraftName);
		spacecraft1.setSpacecraftId(shipclassId);
	
	}	

	@Test
	void findAll_ByDefault_ReturnFluxMultiple() {
		
		Mockito.when(spacecraftReadRepository.findAllBy(Mockito.any(Pageable.class))).thenReturn(Flux.just(spacecraftJpaEntity0, spacecraftJpaEntity1));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(SpacecraftJpaEntity.class))).thenReturn(spacecraft0).thenReturn(spacecraft1);		
		
		Flux<Spacecraft> result = spacecraftReadRepositoryAdapter.findAll(pageRequest);
		
		StepVerifier.create(result)
		.expectNext(spacecraft0)  
		.expectNext(spacecraft1)  
		.verifyComplete();	
		Mockito.verify(spacecraftReadRepository).findAllBy(Mockito.any(Pageable.class));
		Mockito.verify(mapper, Mockito.times(2)).fromJpaToDomain(Mockito.any(SpacecraftJpaEntity.class));
   
	}

	@Test
	void findAll_WhenRepositoryThrowsError_PropagateException() {
		
		RuntimeException ex = new RuntimeException(GlobalConstants.EX_PLAIN);
		Mockito.when(spacecraftReadRepository.findAllBy(Mockito.any(Pageable.class))).thenReturn(Flux.error(ex));

		Flux<Spacecraft> result = spacecraftReadRepositoryAdapter.findAll(pageRequest);
		
		StepVerifier.create(result)
		.expectError(RuntimeException.class)
		.verify();
		
		Mockito.verify(spacecraftReadRepository).findAllBy(Mockito.any(Pageable.class));

	}

	@Test
	void findById_WithValidId_ReturnMonoSpacecraft() {
		
		Mockito.when(spacecraftReadRepository.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(spacecraftJpaEntity0));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(SpacecraftJpaEntity.class))).thenReturn(spacecraft0);		

		Mono<Spacecraft> result = spacecraftReadRepositoryAdapter.findById(spacecraftId0);
		
		StepVerifier.create(result)
		.assertNext( shipclassFound -> {
			org.junit.jupiter.api.Assertions.assertAll(
					()->Assertions.assertThat(shipclassFound).isEqualTo(spacecraft0),
					()->Assertions.assertThat(shipclassFound).isNotEqualTo(spacecraft1),
        			()->Assertions.assertThat(shipclassFound).hasSameHashCodeAs(spacecraft0)
        			);
		})
		.verifyComplete();
		Mockito.verify(spacecraftReadRepository).findById(Mockito.any(UUID.class));
		Mockito.verify(mapper).fromJpaToDomain(Mockito.any(SpacecraftJpaEntity.class));		
		
	}

	@Test
	void findByName_WithValidName_ReturnMonoSpacecraft() {
		
		Mockito.when(spacecraftReadRepository.findBySpacecraftName(Mockito.anyString())).thenReturn(Mono.just(spacecraftJpaEntity0));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(SpacecraftJpaEntity.class))).thenReturn(spacecraft0);		

		Mono<Spacecraft> result = spacecraftReadRepositoryAdapter.findByName("");
		
		StepVerifier.create(result)
		.expectNext(spacecraft0)
		.verifyComplete();
		Mockito.verify(spacecraftReadRepository).findBySpacecraftName(Mockito.anyString());
		Mockito.verify(mapper).fromJpaToDomain(Mockito.any(SpacecraftJpaEntity.class));		
		
	}

}
