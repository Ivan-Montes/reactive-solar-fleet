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
import dev.ime.config.ShipclassMapper;
import dev.ime.domain.model.Shipclass;
import dev.ime.infrastructure.entity.ShipclassJpaEntity;
import dev.ime.infrastructure.repository.ShipclassReadRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ShipclassReadRepositoryAdapterTest {

	@Mock
	private ShipclassReadRepository shipclassReadRepository;

	@Mock
	private ShipclassMapper mapper;

	@InjectMocks
	private ShipclassReadRepositoryAdapter shipclassReadRepositoryAdapter;	

	private ShipclassJpaEntity shipclassJpaEntity0;
	private Shipclass shipclass0;
	private ShipclassJpaEntity shipclassJpaEntity1;
	private Shipclass shipclass1;

	private final UUID shipclassId0 = UUID.randomUUID();
	private final UUID shipclassId1 = UUID.randomUUID();
	private final String shipclassName = "";
	private final String shipclassDescription = "";
	private final PageRequest pageRequest = PageRequest.of(0, 100);

	@BeforeEach
	private void setUp() {		
		
		shipclassJpaEntity0 = new ShipclassJpaEntity(
				shipclassId0,
				shipclassName,
				shipclassDescription);	
		
		shipclassJpaEntity1 = new ShipclassJpaEntity();
		shipclassJpaEntity1.setShipclassId(shipclassId1);
		shipclassJpaEntity1.setShipclassName(shipclassName);
		shipclassJpaEntity1.setShipclassDescription(shipclassDescription);
		
		shipclass0 = new Shipclass(
				shipclassId0,
				shipclassName,
				shipclassDescription);
		
		shipclass1 = new Shipclass();
		shipclass1.setShipclassId(shipclassId1);
		shipclass1.setShipclassName(shipclassName);
		shipclass1.setShipclassDescription(shipclassDescription);
		
	}
	
	@Test
	void findAll_ByDefault_ReturnFluxMultiple() {
		
		Mockito.when(shipclassReadRepository.findAllBy(Mockito.any(Pageable.class))).thenReturn(Flux.just(shipclassJpaEntity0, shipclassJpaEntity1));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(ShipclassJpaEntity.class))).thenReturn(shipclass0).thenReturn(shipclass1);		
		
		Flux<Shipclass> result = shipclassReadRepositoryAdapter.findAll(pageRequest);
		
		StepVerifier.create(result)
		.expectNext(shipclass0)  
		.expectNext(shipclass1)  
		.verifyComplete();	
		Mockito.verify(shipclassReadRepository).findAllBy(Mockito.any(Pageable.class));
		Mockito.verify(mapper, Mockito.times(2)).fromJpaToDomain(Mockito.any(ShipclassJpaEntity.class));
   
	}

	@Test
	void findAll_WhenRepositoryThrowsError_PropagateException() {
		
		RuntimeException ex = new RuntimeException(GlobalConstants.EX_PLAIN);
		Mockito.when(shipclassReadRepository.findAllBy(Mockito.any(Pageable.class))).thenReturn(Flux.error(ex));

		Flux<Shipclass> result = shipclassReadRepositoryAdapter.findAll(pageRequest);
		
		StepVerifier.create(result)
		.expectError(RuntimeException.class)
		.verify();
		
		Mockito.verify(shipclassReadRepository).findAllBy(Mockito.any(Pageable.class));

	}

	@Test
	void findById_WithValidId_ReturnMonoShipclass() {
		
		Mockito.when(shipclassReadRepository.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(shipclassJpaEntity0));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(ShipclassJpaEntity.class))).thenReturn(shipclass0);		

		Mono<Shipclass> result = shipclassReadRepositoryAdapter.findById(shipclassId0);
		
		StepVerifier.create(result)
		.assertNext( shipclassFound -> {
			org.junit.jupiter.api.Assertions.assertAll(
					()->Assertions.assertThat(shipclassFound).isEqualTo(shipclass0),
					()->Assertions.assertThat(shipclassFound).isNotEqualTo(shipclass1),
        			()->Assertions.assertThat(shipclassFound).hasSameHashCodeAs(shipclass0)
        			);
		})
		.verifyComplete();
		Mockito.verify(shipclassReadRepository).findById(Mockito.any(UUID.class));
		Mockito.verify(mapper).fromJpaToDomain(Mockito.any(ShipclassJpaEntity.class));		
		
	}

	@Test
	void findByName_WithValidName_ReturnMonoShipclass() {
		
		Mockito.when(shipclassReadRepository.findByShipclassName(Mockito.anyString())).thenReturn(Mono.just(shipclassJpaEntity0));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(ShipclassJpaEntity.class))).thenReturn(shipclass0);		

		Mono<Shipclass> result = shipclassReadRepositoryAdapter.findByName("");
		
		StepVerifier.create(result)
		.expectNext(shipclass0)
		.verifyComplete();
		Mockito.verify(shipclassReadRepository).findByShipclassName(Mockito.anyString());
		Mockito.verify(mapper).fromJpaToDomain(Mockito.any(ShipclassJpaEntity.class));		
		
	}
	
}
