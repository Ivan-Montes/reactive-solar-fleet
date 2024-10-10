package dev.ime.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import dev.ime.infrastructure.entity.CrewMemberJpaEntity;
import reactor.core.publisher.Flux;

public interface CrewMemberReadRepository extends ReactiveCrudRepository<CrewMemberJpaEntity, UUID>, ReactiveSortingRepository<CrewMemberJpaEntity, UUID> {

    Flux<CrewMemberJpaEntity> findByCrewMemberNameAndCrewMemberSurname(String name, String surname);
    Flux<CrewMemberJpaEntity> findAllBy(Pageable pageable);

}
