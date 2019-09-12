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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "certificate")
public class Certificate {
	
	/**
	 * Constructor used when creating a certificate that belongs to a root CA
	 */
	public Certificate(CA issuer, Integer validity, Integer confidence, Integer assurance,
			Integer minChain, Integer chainRange) {
		super();
		this.issuer = issuer;
		this.subject = issuer;
		this.validity = validity;
		this.confidence = confidence;
		this.assurance = assurance;
		this.minChain = minChain;
		this.chainRange = chainRange;
	}

	/**
	 * Constructor used when creating a certificate that belongs to a sub CA
	 */
	public Certificate(CA issuer, CA subject, Integer validity, Integer psId,
			Integer confidence, Integer assurance, String cracaId, Integer crlSeries, Integer minChain,
			Integer chainRange) {
		super();
		this.issuer = issuer;
		this.subject = subject;
		this.validity = validity;
		this.psId = psId;
		this.confidence = confidence;
		this.assurance = assurance;
		this.cracaId = cracaId;
		this.crlSeries = crlSeries;
		this.minChain = minChain;
		this.chainRange = chainRange;
	}
	
	
	public Certificate() {
		
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="certificate_id")
	private Long certId;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name="issuer_id", nullable=false)
	private CA issuer;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name="subject_id", nullable=false)
	private CA subject;
	
	@Column(name = "validity")
	@NotNull(message = "*Please provide a validity period")
	private Integer validity;
	

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "certificate_regions", joinColumns = @JoinColumn(name = "certificate_id"), inverseJoinColumns = @JoinColumn(name = "region_id"))
	private List<Region> regions;
	
	@Column(name = "psid")
	private Integer psId;
	
	@Column(name = "confidence")
	@NotNull(message = "*Please provide the confidence level")
	private Integer confidence;
	
	@Column(name = "assurance")
	@NotNull(message = "*Please provide the assurance level")
	private Integer assurance;
	
	@Column(name = "cracaid")
	private String cracaId;
	
	@Column(name = "crl_series")
	private Integer crlSeries;
	
	@Column(name = "min_chain")
	private Integer minChain;

	@Column(name = "chain_range")
	private Integer chainRange;
	
	@Column(name = "encoded")
	private byte[] encoded;

	public Long getCertId() {
		return certId;
	}
	@Column(name="certificate_hash_id")
	private byte[] hashedId;

	public void setCertId(Long certId) {
		this.certId = certId;
	}

	public CA getIssuer() {
		return issuer;
	}

	public void setIssuer(CA issuer) {
		this.issuer = issuer;
	}

	public CA getSubject() {
		return subject;
	}

	public void setSubject(CA subject) {
		this.subject = subject;
	}

	public Integer getValidity() {
		return validity;
	}

	public void setValidity(Integer validity) {
		this.validity = validity;
	}

	public List<Region> getRegions() {
		return regions;
	}

	public void setRegions(List<Region> regions) {
		this.regions = regions;
	}

	public Integer getPsId() {
		return psId;
	}

	public void setPsId(Integer psId) {
		this.psId = psId;
	}

	public Integer getConfidence() {
		return confidence;
	}

	public void setConfidence(Integer confidence) {
		this.confidence = confidence;
	}

	public Integer getAssurance() {
		return assurance;
	}

	public void setAssurance(Integer assurance) {
		this.assurance = assurance;
	}

	public String getCracaId() {
		return cracaId;
	}

	public void setCracaId(String cracaId) {
		this.cracaId = cracaId;
	}

	public Integer getCrlSeries() {
		return crlSeries;
	}

	public void setCrlSeries(Integer crlSeries) {
		this.crlSeries = crlSeries;
	}

	public Integer getMinChain() {
		return minChain;
	}

	public void setMinChain(Integer minChain) {
		this.minChain = minChain;
	}

	public Integer getChainRange() {
		return chainRange;
	}

	public void setChainRange(Integer chainRange) {
		this.chainRange = chainRange;
	}

	public byte[] getEncoded() {
		return encoded;
	}

	public void setEncoded(byte[] encoded) {
		this.encoded = encoded;
	}

	public byte[] getHashedId() {
		return hashedId;
	}

	public void setHashedId(byte[] hashedId) {
		this.hashedId = hashedId;
	}


}
