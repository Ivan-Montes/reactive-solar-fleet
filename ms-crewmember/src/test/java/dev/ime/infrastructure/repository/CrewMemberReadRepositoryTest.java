package dev.ime.infrastructure.repository;


import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import dev.ime.config.GlobalConstants;
import dev.ime.infrastructure.entity.CrewMemberJpaEntity;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Testcontainers
@DataR2dbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CrewMemberReadRepositoryTest {

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;
    
    @Autowired
    private CrewMemberReadRepository crewMemberReadRepository;

	@SuppressWarnings("resource")
	@Container
	@ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))   
    .withInitScript("init.sql");

	private final UUID crewMemberId = UUID.randomUUID();
	private final String crewMemberName = "Walter";
	private final String crewMemberSurname = "Kovacs";
	private final UUID positionId = UUID.randomUUID();
	private final UUID spacecraftId = UUID.randomUUID();
    
	@BeforeEach
	void setUp() {
		
		CrewMemberJpaEntity entity = new CrewMemberJpaEntity(crewMemberId, crewMemberName, crewMemberSurname, positionId, spacecraftId);
        StepVerifier.create(r2dbcEntityTemplate.insert(entity))
            .expectNextCount(1)
            .verifyComplete();
        
	}

    @AfterEach
    void tearDown() {
    	
        StepVerifier.create(r2dbcEntityTemplate.delete(CrewMemberJpaEntity.class)
            .all()
            .then())
            .verifyComplete();
        
    }   

	@Test
    void connectionEstablished() {
		
        Assertions.assertThat(postgres.isCreated()).isTrue();
        Assertions.assertThat(postgres.isRunning()).isTrue();
        
    }

    @Test
    void findAll_ShouldReturnAll() {
    	
        StepVerifier.create(crewMemberReadRepository.findAll())
            .expectNextCount(1)
            .verifyComplete();
        
    }

    @Test
    void findById_ShouldReturnOneResult() {
    	
        StepVerifier.create(crewMemberReadRepository.findById(crewMemberId))
            .expectNextMatches(entity -> 
            entity.getCrewMemberId().equals(crewMemberId) &&
            entity.getCrewMemberName().equals(crewMemberName) &&
            entity.getCrewMemberSurname().equals(crewMemberSurname))
            .verifyComplete();
        
    }    

    @Test
    void findById_WithNonExistentId_ShouldReturnEmpty() {
    	
        UUID nonExistentId = UUID.randomUUID();
        StepVerifier.create(crewMemberReadRepository.findById(nonExistentId))
            .verifyComplete();
        
    }

    @Test
    void findAll_WithMultipleEntities_ShouldReturnAllEntities() {
        
        Flux<CrewMemberJpaEntity> insertFlux = Flux.just(
            createEntity(),
            createEntity(),
            createEntity()
        ).flatMap(entity -> r2dbcEntityTemplate.insert(entity));

        StepVerifier.create(insertFlux)
            .expectNextCount(3)
            .verifyComplete();

        StepVerifier.create(crewMemberReadRepository.findAll())
            .expectNextCount(4)
            .verifyComplete();
        
    }

    @Test
    void findByCrewMemberNameAndCrewMemberSurname_WithExistentOne_ReturnIt() {
    	
    	StepVerifier.create(crewMemberReadRepository.findByCrewMemberNameAndCrewMemberSurname(crewMemberName, crewMemberSurname))
    	.assertNext( entityFound -> {
    		org.junit.jupiter.api.Assertions.assertAll(
    				()->Assertions.assertThat(entityFound.getCrewMemberName()).isEqualTo(crewMemberName),
    				()->Assertions.assertThat(entityFound.getCrewMemberSurname()).isEqualTo(crewMemberSurname)
    				);
    	})
    	.verifyComplete();
    	
    }
    
    private CrewMemberJpaEntity createEntity() {
    	
        return new CrewMemberJpaEntity(UUID.randomUUID(), generateRandomName(), generateRandomName(), UUID.randomUUID(), UUID.randomUUID());
   
    }
    
	private String generateRandomName() {
		
		return GlobalConstants.CREWMEMBER_CAT + ":" + UUID.randomUUID().toString().substring(0, 8);
	
	}
	

}
