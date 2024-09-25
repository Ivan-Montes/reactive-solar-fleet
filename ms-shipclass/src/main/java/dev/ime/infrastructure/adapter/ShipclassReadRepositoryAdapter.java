package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.config.ShipclassMapper;
import dev.ime.domain.model.Shipclass;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.infrastructure.repository.ShipclassReadRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ShipclassReadRepositoryAdapter implements ReadRepositoryPort<Shipclass> {

	private final ShipclassReadRepository shipclassReadRepository;
	private final ShipclassMapper mapper;
	
	public ShipclassReadRepositoryAdapter(ShipclassReadRepository shipclassReadRepository, ShipclassMapper mapper) {
		super();
		this.shipclassReadRepository = shipclassReadRepository;
		this.mapper = mapper;
	}

	@Override
	public Flux<Shipclass> findAll(Pageable pageable) {

		return shipclassReadRepository
		.findAllBy(pageable)
		.map(mapper::fromJpaToDomain);
	}

	@Override
	public Mono<Shipclass> findById(UUID id) {
		
		return shipclassReadRepository
				.findById(id)
				.map(mapper::fromJpaToDomain);	
	}

	@Override
	public Mono<Shipclass> findByName(String name) {
		
		return shipclassReadRepository
				.findByShipclassName(name)
				.map(mapper::fromJpaToDomain);
	}

}
