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


@Table(name = "positions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PositionJpaEntity {

	@Id
	@Column( value = "position_id" )
	private UUID positionId;
	
	@Column( value = "position_name" )
	private String positionName;
	
	@Column( value = "position_description" )
	private String positionDescription;
	
}
