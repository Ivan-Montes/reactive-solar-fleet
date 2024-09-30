package dev.ime.infrastructure.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(value = "spacecrafts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SpacecraftJpaEntity {

	@Id
	@Column(value = "spacecraft_id")
	private UUID spacecraftId;

	@Column(value = "spacecraft_name")
	private String spacecraftName;

	@Column(value = "shipclass_id")
	private UUID shipclassId;
	
}
