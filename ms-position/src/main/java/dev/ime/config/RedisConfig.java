package dev.ime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import dev.ime.infrastructure.entity.CrewMemberRedisEntity;

@Configuration
public class RedisConfig {

    @Bean
    ReactiveRedisTemplate<String, CrewMemberRedisEntity> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<CrewMemberRedisEntity> serializer = new Jackson2JsonRedisSerializer<>(CrewMemberRedisEntity.class);
        
        RedisSerializationContext.RedisSerializationContextBuilder<String, CrewMemberRedisEntity> builder =
            RedisSerializationContext.newSerializationContext( RedisSerializer.string() );
        
        RedisSerializationContext<String, CrewMemberRedisEntity> context = builder
            .value(serializer)
            .build();
        
        return new ReactiveRedisTemplate<>(factory, context);
        
    }

    @Bean
    ReactiveRedisTemplate<String, String> stringReactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext.RedisSerializationContextBuilder<String, String> builder =
            RedisSerializationContext.newSerializationContext(RedisSerializer.string());

        RedisSerializationContext<String, String> context = builder
            .value(RedisSerializer.string())
            .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

}
