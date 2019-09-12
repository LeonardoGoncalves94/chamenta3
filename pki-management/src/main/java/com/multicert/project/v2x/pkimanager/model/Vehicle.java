package com.multicert.project.v2x.pkimanager.model;

import java.security.PublicKey;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.FetchType;


@Entity
@Table(name = "vehicle")
public class Vehicle {
	
	public Vehicle(String vehicleId, PublicKey publicKey, Profile profile) {
		super();
		this.vehicleId = vehicleId;
		this.canonicalPublicKey = publicKey;
		this.profile = profile;
		
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
	
	@Column(name="at_count")
	private int aTCount = 0;
	
	public int getaTCount() {
		return aTCount;
	}

	public void setaTCount(int aTCount) {
		this.aTCount = aTCount;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name="profile_name", nullable=false)
	private Profile profile;
	
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

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
	
}
