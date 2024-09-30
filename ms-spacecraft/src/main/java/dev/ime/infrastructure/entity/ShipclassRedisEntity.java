package dev.ime.infrastructure.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;

@RedisHash
@AllArgsConstructor
@Getter
public class ShipclassRedisEntity {
	
	@Id
	private UUID shipclassId;
	
}
