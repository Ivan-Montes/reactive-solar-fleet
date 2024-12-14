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
import dev.ime.infrastructure.entity.ShipclassJpaEntity;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Testcontainers
@DataR2dbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShipclassReadRepositoryTest {

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;
    
    @Autowired
    private ShipclassReadRepository shipclassReadRepository;

	@SuppressWarnings("resource")
	@Container
	@ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))   
    .withInitScript("init.sql");

	private final UUID shipclassId = UUID.randomUUID();
	private final String shipclassName = "Defensor";
	private final String shipclassDescription = "Class for defensive tasks";
    
	@BeforeEach
	void setUp() {
		
		ShipclassJpaEntity entity = new ShipclassJpaEntity(shipclassId, shipclassName, shipclassDescription);
        StepVerifier.create(r2dbcEntityTemplate.insert(entity))
            .expectNextCount(1)
            .verifyComplete();
        
	}

    @AfterEach
    void tearDown() {
    	
        StepVerifier.create(r2dbcEntityTemplate.delete(ShipclassJpaEntity.class)
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
    	
        StepVerifier.create(shipclassReadRepository.findAll())
            .expectNextCount(1)
            .verifyComplete();
        
    }

    @Test
    void findById_ShouldReturnOneResult() {
    	
        StepVerifier.create(shipclassReadRepository.findById(shipclassId))
            .expectNextMatches(entity -> 
            entity.getShipclassId().equals(shipclassId) &&
            entity.getShipclassName().equals(shipclassName) &&
            entity.getShipclassDescription().equals(shipclassDescription))
            .verifyComplete();
        
    }

    @Test
    void findById_WithNonExistentId_ShouldReturnEmpty() {
    	
        UUID nonExistentId = UUID.randomUUID();
        StepVerifier.create(shipclassReadRepository.findById(nonExistentId))
            .verifyComplete();
        
    }

    @Test
    void findAll_WithMultipleEntities_ShouldReturnAllEntities() {
        
        Flux<ShipclassJpaEntity> insertFlux = Flux.just(
            createEntity(),
            createEntity(),
            createEntity()
        ).flatMap(entity -> r2dbcEntityTemplate.insert(entity));

        StepVerifier.create(insertFlux)
            .expectNextCount(3)
            .verifyComplete();

        StepVerifier.create(shipclassReadRepository.findAll())
            .expectNextCount(4)
            .verifyComplete();
        
    }

    @Test
    void findByName_WithExistentOne_ReturnIt() {
    	
    	StepVerifier.create(shipclassReadRepository.findByShipclassName(shipclassName))
    	.assertNext( entityFound -> {
    		org.junit.jupiter.api.Assertions.assertAll(
    				()->Assertions.assertThat(entityFound.getShipclassName()).isEqualTo(shipclassName)
    				);
    	})
    	.verifyComplete();
    	
    }
    
    private ShipclassJpaEntity createEntity() {
    	
        return new ShipclassJpaEntity(UUID.randomUUID(), generateRandomName(), generateRandomName());
   
    }
    
	private String generateRandomName() {
		
		return GlobalConstants.SHIPCLASS_CAT + ":" + UUID.randomUUID().toString().substring(0, 8);
	
	}
	
}
