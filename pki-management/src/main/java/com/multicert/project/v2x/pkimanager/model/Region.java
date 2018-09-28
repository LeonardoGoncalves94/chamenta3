package com.multicert.project.v2x.pkimanager.model;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
/**
 * This Table holds the Countries encoded as integer values (ISO 3166 Codes (Countries))
 *
 */
@Entity
@Table(name = "region")
public class Region {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "region_id")
	private Long regionId;
	
	@Column(name = "region_name")
	@Length(min = 2, max = 30)
	@NotEmpty(message = "*Please provide a Country name")
	private String regionName;
	
	@Column(name = "region_number")
	@NotEmpty(message = "*Please provide a Country number")
	private int regionNumber;

	public Long getRegionId() {
		return regionId;
	}

	public void setRegionId(Long regionId) {
		this.regionId = regionId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public int getRegionNumber() {
		return regionNumber;
	}

	public void setRegionNumber(int regionNumber) {
		this.regionNumber = regionNumber;
	}



	

}
