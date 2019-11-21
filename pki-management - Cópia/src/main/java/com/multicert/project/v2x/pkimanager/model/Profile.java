package com.multicert.project.v2x.pkimanager.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "profile")
public class Profile {
	
	public Profile(String profileName, String assuranceLevel, 
			String confidenceLebel, int numberOfATs, 
			Vehicle vehicle, List <Region> allowedcountries) {
		super();
		this.profileName = profileName;
		
	}
	
	public Profile() {
	}

	@Id
	@Column(name = "profile_name")
	private String profileName;
	
	@Column(name = "vehicle_type")
	@NotEmpty()
	private int vehicleType;	
	
	public int getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(int vehicleType) {
		this.vehicleType = vehicleType;
	}

	@Column(name = "assurance_level")
	@NotEmpty()
	private int assuranceLevel;	
	
	@Column(name = "enrollment_period")
	@NotEmpty()
	private int enrollmentPeriod;	
	
	public int getEnrollmentPeriod() {
		return enrollmentPeriod;
	}

	public void setEnrollmentPeriod(int enrollmentPeriod) {
		this.enrollmentPeriod = enrollmentPeriod;
	}

	@Column(name = "confidence_level")
	@NotEmpty()
	private int confidenceLevel;	
	
	@Column(name = "number_of_ATs")
	@NotEmpty()
	private int numberOfATs;
	
	@OneToMany(mappedBy = "profile")
	private List<Vehicle> vehicle;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "profile_regions", joinColumns = @JoinColumn(name = "name"), inverseJoinColumns = @JoinColumn(name = "region_id"))
	private List<Region> regions;

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public int getAssuranceLevel() {
		return assuranceLevel;
	}

	public void setAssuranceLevel(int assuranceLevel) {
		this.assuranceLevel = assuranceLevel;
	}

	public int getConfidenceLevel() {
		return confidenceLevel;
	}

	public void setConfidenceLevel(int confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}

	public int getNumberOfATs() {
		return numberOfATs;
	}

	public void setNumberOfATs(int numberOfATs) {
		this.numberOfATs = numberOfATs;
	}

	public List<Vehicle> getVehicle() {
		return vehicle;
	}

	public void setVehicle(List<Vehicle> vehicle) {
		this.vehicle = vehicle;
	}

	public List<Region> getRegions() {
		return regions;
	}

	public void setRegions(List<Region> regions) {
		this.regions = regions;
	}
	

}
