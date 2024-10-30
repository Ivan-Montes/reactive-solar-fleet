package dev.ime.config;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import dev.ime.infrastructure.entity.PositionRedisEntity;
import dev.ime.infrastructure.entity.SpacecraftRedisEntity;

@ExtendWith(MockitoExtension.class)
class RedisConfigTest {
	
	@Mock
	private ReactiveRedisConnectionFactory factory;
	
	@InjectMocks
	private RedisConfig redisConfig;
	
	
	@Test
	void spacecraftReactiveRedisTemplate_ShouldReturnReactiveRedisTemplate() {
		
		ReactiveRedisTemplate<String, SpacecraftRedisEntity> template = redisConfig.spacecraftReactiveRedisTemplate(factory);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(template).isNotNull()
				);
	}

	@Test
	void positionReactiveRedisTemplate_ShouldReturnReactiveRedisTemplate() {
		
		ReactiveRedisTemplate<String, PositionRedisEntity> template = redisConfig.positionReactiveRedisTemplate(factory);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(template).isNotNull()
				);
	}

}
