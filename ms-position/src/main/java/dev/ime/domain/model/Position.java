package dev.ime.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Position {

	private UUID positionId;
	private String positionName;
	private String positionDescription;
	
	public Position() {
		super();
	}

	public Position(UUID positionId, String positionName, String positionDescription) {
		super();
		this.positionId = positionId;
		this.positionName = positionName;
		this.positionDescription = positionDescription;
	}

	public UUID getPositionId() {
		return positionId;
	}

	public void setPositionId(UUID positionId) {
		this.positionId = positionId;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public String getPositionDescription() {
		return positionDescription;
	}

	public void setPositionDescription(String positionDescription) {
		this.positionDescription = positionDescription;
	}

	@Override
	public int hashCode() {
		return Objects.hash(positionDescription, positionId, positionName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		return Objects.equals(positionDescription, other.positionDescription)
				&& Objects.equals(positionId, other.positionId) && Objects.equals(positionName, other.positionName);
	}

	@Override
	public String toString() {
		return "Position [positionId=" + positionId + ", positionName=" + positionName + ", positionDescription="
				+ positionDescription + "]";
	}
	
	
}
