package dev.ime.infrastructure.entity;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document("events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class EventMongoEntity {

	@Id
	@Field( name = "event_id")
	private UUID eventId;
	
	@Field( name = "event_category")
	private String eventCategory;
	
	@Field( name = "event_type")
	private String eventType;
	
	@Field( name = "event_timestamp")
	private Instant eventTimestamp;
	
	@Field( name = "event_data")
	private Map<String, Object> eventData;	
	
}
