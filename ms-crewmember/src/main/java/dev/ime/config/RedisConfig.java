package dev.ime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import dev.ime.infrastructure.entity.PositionRedisEntity;
import dev.ime.infrastructure.entity.SpacecraftRedisEntity;

@Configuration
public class RedisConfig {

    @Bean
    ReactiveRedisTemplate<String, SpacecraftRedisEntity> spacecraftReactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<SpacecraftRedisEntity> serializer = new Jackson2JsonRedisSerializer<>(SpacecraftRedisEntity.class);
        
        RedisSerializationContext.RedisSerializationContextBuilder<String, SpacecraftRedisEntity> builder =
            RedisSerializationContext.newSerializationContext( RedisSerializer.string() );
        
        RedisSerializationContext<String, SpacecraftRedisEntity> context = builder
            .value(serializer)
            .build();
        
        return new ReactiveRedisTemplate<>(factory, context);
        
    }

    @Bean
    ReactiveRedisTemplate<String, PositionRedisEntity> positionReactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<PositionRedisEntity> serializer = new Jackson2JsonRedisSerializer<>(PositionRedisEntity.class);
        
        RedisSerializationContext.RedisSerializationContextBuilder<String, PositionRedisEntity> builder =
            RedisSerializationContext.newSerializationContext( RedisSerializer.string() );
        
        RedisSerializationContext<String, PositionRedisEntity> context = builder
            .value(serializer)
            .build();
        
        return new ReactiveRedisTemplate<>(factory, context);
        
    }
    
}
