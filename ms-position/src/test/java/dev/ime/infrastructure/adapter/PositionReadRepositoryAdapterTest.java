package dev.ime.infrastructure.adapter;


import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import dev.ime.config.GlobalConstants;
import dev.ime.config.PositionMapper;
import dev.ime.domain.model.Position;
import dev.ime.infrastructure.entity.PositionJpaEntity;
import dev.ime.infrastructure.repository.PositionReadRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PositionReadRepositoryAdapterTest {

	@Mock
	private PositionReadRepository positionReadRepository;
	
	@Mock
	private PositionMapper mapper;

	@InjectMocks
	private PositionReadRepositoryAdapter positionReadRepositoryAdapter;

	private PositionJpaEntity positionJpaEntity0;
	private Position position0;
	private PositionJpaEntity positionJpaEntity1;
	private Position position1;
	private final UUID positionId0 = UUID.randomUUID();
	private final UUID positionId1 = UUID.randomUUID();
	private final String positionName = "";
	private final String positionDescription = "";
	private final PageRequest pageRequest = PageRequest.of(0,99);
	
	@BeforeEach
	private void setUp() {
		
		
		positionJpaEntity0 = new PositionJpaEntity(
				positionId0,
				positionName,
				positionDescription);		

		positionJpaEntity1 = new PositionJpaEntity();
		positionJpaEntity1.setPositionId(positionId1);
		positionJpaEntity1.setPositionName(positionName);
		positionJpaEntity1.setPositionDescription(positionDescription);
		
		position0 = new Position(
				positionId0,
				positionName,
				positionDescription);
		
		position1 = new Position();
		position1.setPositionId(positionId1);
		position1.setPositionName(positionName);
		position1.setPositionDescription(positionDescription);
		
	}
	
	@Test
	void findAll_ByDefault_ReturnFluxMultiple() {
		
		Mockito.when(positionReadRepository.findAllBy(Mockito.any(Pageable.class))).thenReturn(Flux.just(positionJpaEntity0,positionJpaEntity1));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(PositionJpaEntity.class))).thenReturn(position0).thenReturn(position1);		
		
		Flux<Position> result = positionReadRepositoryAdapter.findAll(pageRequest);
		
		StepVerifier.create(result)
		.expectNext(position0)  
		.expectNext(position1)  
		.verifyComplete();	
		Mockito.verify(positionReadRepository).findAllBy(Mockito.any(Pageable.class));
		Mockito.verify(mapper, Mockito.times(2)).fromJpaToDomain(Mockito.any(PositionJpaEntity.class));
   
	}

	@Test
	void findAll_WhenRepositoryThrowsError_PropagateException() {
		
		RuntimeException ex = new RuntimeException(GlobalConstants.EX_PLAIN);
		Mockito.when(positionReadRepository.findAllBy(Mockito.any(Pageable.class))).thenReturn(Flux.error(ex));

		Flux<Position> result = positionReadRepositoryAdapter.findAll(pageRequest);
		
		StepVerifier.create(result)
		.expectError(RuntimeException.class)
		.verify();
		
		Mockito.verify(positionReadRepository).findAllBy(Mockito.any(Pageable.class));

	}

	@Test
	void findById_WithValidId_ReturnMonoPosition() {
		
		Mockito.when(positionReadRepository.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(positionJpaEntity0));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(PositionJpaEntity.class))).thenReturn(position0);		

		Mono<Position> result = positionReadRepositoryAdapter.findById(positionId0);
		
		StepVerifier.create(result)
		.assertNext( entityFound -> {
			org.junit.jupiter.api.Assertions.assertAll(
					()->Assertions.assertThat(entityFound).isEqualTo(position0),
					()->Assertions.assertThat(entityFound).isNotEqualTo(position1),
        			()->Assertions.assertThat(entityFound).hasSameHashCodeAs(position0)
        			);
		})
		.verifyComplete();
		Mockito.verify(positionReadRepository).findById(Mockito.any(UUID.class));
		Mockito.verify(mapper).fromJpaToDomain(Mockito.any(PositionJpaEntity.class));		
		
	}

	@Test
	void findByName_WithValidName_ReturnMonoPosition() {
		
		Mockito.when(positionReadRepository.findByPositionName(Mockito.anyString())).thenReturn(Mono.just(positionJpaEntity0));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(PositionJpaEntity.class))).thenReturn(position0);		

		Mono<Position> result = positionReadRepositoryAdapter.findByName("");
		
		StepVerifier.create(result)
		.expectNext(position0)
		.verifyComplete();
		Mockito.verify(positionReadRepository).findByPositionName(Mockito.anyString());
		Mockito.verify(mapper).fromJpaToDomain(Mockito.any(PositionJpaEntity.class));		
		
	}
	
}
