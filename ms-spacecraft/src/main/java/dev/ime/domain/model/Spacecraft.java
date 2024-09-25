package dev.ime.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Spacecraft {

	private UUID spacecraftId;
	private String spacecraftName;
	private UUID shipclassId;
	
	public Spacecraft() {
		super();
	}

	public Spacecraft(UUID spacecraftId, String spacecraftName, UUID shipclassId) {
		super();
		this.spacecraftId = spacecraftId;
		this.spacecraftName = spacecraftName;
		this.shipclassId = shipclassId;
	}

	public UUID getSpacecraftId() {
		return spacecraftId;
	}

	public void setSpacecraftId(UUID spacecraftId) {
		this.spacecraftId = spacecraftId;
	}

	public String getSpacecraftName() {
		return spacecraftName;
	}

	public void setSpacecraftName(String spacecraftName) {
		this.spacecraftName = spacecraftName;
	}

	public UUID getShipclassId() {
		return shipclassId;
	}

	public void setShipclassId(UUID shipclassId) {
		this.shipclassId = shipclassId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(shipclassId, spacecraftId, spacecraftName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Spacecraft other = (Spacecraft) obj;
		return Objects.equals(shipclassId, other.shipclassId) && Objects.equals(spacecraftId, other.spacecraftId)
				&& Objects.equals(spacecraftName, other.spacecraftName);
	}

	@Override
	public String toString() {
		return "Spacecraft [spacecraftId=" + spacecraftId + ", spacecraftName=" + spacecraftName + ", shipclassId="
				+ shipclassId + "]";
	}	
	
}
