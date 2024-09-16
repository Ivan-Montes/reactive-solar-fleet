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

@Table(value = "shipclasses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ShipclassJpaEntity {

	@Id
	@Column( value = "shipclass_id" )
	private UUID shipclassId;
	
	@Column( value = "shipclass_name" )
	private String shipclassName;
	
	@Column( value = "shipclass_description" )
	private String shipclassDescription;
	
}
