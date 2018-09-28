package com.multicert.project.v2x.pkimanager.model;

import java.security.PublicKey;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;

import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;


@Entity
@Table(name = "vehicle")
public class Vehicle {
	
	public Vehicle(String vehicleId, PublicKey publicKey, String profileName) {
		super();
		this.vehicleId = vehicleId;
		this.canonicalPublicKey = publicKey;
		this.profileName = profileName;
		
	}
	
	public Vehicle() {
		
	}

	@Id
	@Column(name="vehicle_id")
	@Length(min = 5, max = 20)
	@NotEmpty
	private String vehicleId;
	
	@Column(name="pubKey")
	private PublicKey canonicalPublicKey;
	
	
	@Column(name="vehicle_profile")
	@NotEmpty
	private String profileName;
	
	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public PublicKey getPublicKey() {
		return canonicalPublicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.canonicalPublicKey = publicKey;
	}

	public PublicKey getCanonicalPublicKey() {
		return canonicalPublicKey;
	}

	public void setCanonicalPublicKey(PublicKey canonicalPublicKey) {
		this.canonicalPublicKey = canonicalPublicKey;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	
}
