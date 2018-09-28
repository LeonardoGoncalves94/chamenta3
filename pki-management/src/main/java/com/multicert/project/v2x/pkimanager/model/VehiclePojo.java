package com.multicert.project.v2x.pkimanager.model;

/**
 * Simplification of the Database object Vehicle, this class contains:
 * 
 * The vehicleId which represents the vehicle's unique name.
 * The canonicalPublicKey representing the vehicle's encoded public key. Such public key must be an encoded PublicVerificationKey structure from EtsiTs103 097
 * The vehicleType represents a specific type of vehicle, ex. ambulance, truck etc..
 *
 */
public class VehiclePojo {
	
	public VehiclePojo() {
	
	}
	
	private String vehicleId;
	
	private String canonicalPublicKey;
	
	private int vehicleType;

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getPublicKey() {
		return canonicalPublicKey;
	}

	public void setPublicKey(String publicKey) {
		this.canonicalPublicKey = publicKey;
	}

	public String getCanonicalPublicKey() {
		return canonicalPublicKey;
	}

	public void setCanonicalPublicKey(String canonicalPublicKey) {
		this.canonicalPublicKey = canonicalPublicKey;
	}

	public int getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(int vehicleType) {
		this.vehicleType = vehicleType;
	}
	
}
