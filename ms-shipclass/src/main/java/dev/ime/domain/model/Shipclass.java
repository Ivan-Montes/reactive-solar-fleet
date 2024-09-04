package dev.ime.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Shipclass {
	
	private UUID shipclassId;
	private String shipclassName;
	private String shipclassDescription;
	
	public Shipclass() {
		super();
	}
	
	public Shipclass(UUID shipclassId, String shipclassName, String shipclassDescription) {
		super();
		this.shipclassId = shipclassId;
		this.shipclassName = shipclassName;
		this.shipclassDescription = shipclassDescription;
	}
	
	public UUID getShipclassId() {
		return shipclassId;
	}
	
	public void setShipclassId(UUID shipclassId) {
		this.shipclassId = shipclassId;
	}
	
	public String getShipclassName() {
		return shipclassName;
	}
	
	public void setShipclassName(String shipclassName) {
		this.shipclassName = shipclassName;
	}
	
	public String getShipclassDescription() {
		return shipclassDescription;
	}
	
	public void setShipclassDescription(String shipclassDescription) {
		this.shipclassDescription = shipclassDescription;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(shipclassDescription, shipclassId, shipclassName);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Shipclass other = (Shipclass) obj;
		return Objects.equals(shipclassDescription, other.shipclassDescription)
				&& Objects.equals(shipclassId, other.shipclassId) && Objects.equals(shipclassName, other.shipclassName);
	}

	@Override
	public String toString() {
		return "Shipclass [shipclassId=" + shipclassId + ", shipclassName=" + shipclassName + ", shipclassDescription="
				+ shipclassDescription + "]";
	}	
	
}
