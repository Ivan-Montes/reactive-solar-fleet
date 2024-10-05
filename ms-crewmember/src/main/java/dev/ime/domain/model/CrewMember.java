package dev.ime.domain.model;

import java.util.Objects;
import java.util.UUID;

public class CrewMember {

	private UUID crewMemberId;
	private String crewMemberName;
	private String crewMemberSurname;
	private UUID positionId;
	private UUID spacecraftId;
	
	public CrewMember() {
		super();
	}
	
	public CrewMember(UUID crewMemberId, String crewMemberName, String crewMemberSurname, UUID positionId,
			UUID spacecraftId) {
		super();
		this.crewMemberId = crewMemberId;
		this.crewMemberName = crewMemberName;
		this.crewMemberSurname = crewMemberSurname;
		this.positionId = positionId;
		this.spacecraftId = spacecraftId;
	}

	public UUID getCrewMemberId() {
		return crewMemberId;
	}

	public void setCrewMemberId(UUID crewMemberId) {
		this.crewMemberId = crewMemberId;
	}

	public String getCrewMemberName() {
		return crewMemberName;
	}

	public void setCrewMemberName(String crewMemberName) {
		this.crewMemberName = crewMemberName;
	}

	public String getCrewMemberSurname() {
		return crewMemberSurname;
	}

	public void setCrewMemberSurname(String crewMemberSurname) {
		this.crewMemberSurname = crewMemberSurname;
	}

	public UUID getPositionId() {
		return positionId;
	}

	public void setPositionId(UUID positionId) {
		this.positionId = positionId;
	}

	public UUID getSpacecraftId() {
		return spacecraftId;
	}

	public void setSpacecraftId(UUID spacecraftId) {
		this.spacecraftId = spacecraftId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(crewMemberId, crewMemberName, crewMemberSurname, positionId, spacecraftId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CrewMember other = (CrewMember) obj;
		return Objects.equals(crewMemberId, other.crewMemberId) && Objects.equals(crewMemberName, other.crewMemberName)
				&& Objects.equals(crewMemberSurname, other.crewMemberSurname)
				&& Objects.equals(positionId, other.positionId) && Objects.equals(spacecraftId, other.spacecraftId);
	}

	@Override
	public String toString() {
		return "CrewMember [crewMemberId=" + crewMemberId + ", crewMemberName=" + crewMemberName
				+ ", crewMemberSurname=" + crewMemberSurname + ", positionId=" + positionId + ", spacecraftId="
				+ spacecraftId + "]";
	}
	
}
