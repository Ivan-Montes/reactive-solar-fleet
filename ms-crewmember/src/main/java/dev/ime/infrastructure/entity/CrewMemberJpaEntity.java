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

@Table(name = "crewmembers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CrewMemberJpaEntity {

	@Id
	@Column( value = "crewmember_id" )
	private UUID crewMemberId;
	
	@Column( value = "crewmember_name" )
	private String crewMemberName;
	
	@Column( value = "crewmember_surname" )
	private String crewMemberSurname;
	
	@Column( value = "position_id" )
	private UUID positionId;
	
	@Column( value = "spacecraft_id" )
	private UUID spacecraftId;
	
}
