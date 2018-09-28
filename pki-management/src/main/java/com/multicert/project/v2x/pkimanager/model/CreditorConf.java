/**
 * 
 */
package com.multicert.project.v2x.pkimanager.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author ccardoso
 *
 */
@Entity
@Table(name = "creditor_conf")
public class CreditorConf {

	@Id
	@Column(name = "creditor_id")
	@Length(min = 5, max = 20)
	@NotEmpty
	private String creditorId;

	@Column(name = "creditor_name")
	@NotEmpty(message = "*Please provide the Creditor Name")
	private String creditorName;

	@Column(name = "creditor_address")
	@Length(min = 2, max = 200)
	private String creditorAddress;

	@Column(name = "creditor_country")
	@NotEmpty(message = "*Please provide Creditor Country")
	private String creditorCountry;

	@Column(name = "creditor_bank_bic")
	@Length(min = 8, max = 20, message = "Bank BIC invalid lenght")
	@NotEmpty(message = "*Please provide Creditor Bank BIC")
	private String creditorBankBic;


	public String getCreditorId() {
		return creditorId;
	}

	public void setCreditorId(String creditorId) {
		this.creditorId = creditorId;
	}

	public String getCreditorName() {
		return creditorName;
	}

	public void setCreditorName(String creditorName) {
		this.creditorName = creditorName;
	}

	public String getCreditorAddress() {
		return creditorAddress;
	}

	public void setCreditorAddress(String creditorAddress) {
		this.creditorAddress = creditorAddress;
	}

	public String getCreditorCountry() {
		return creditorCountry;
	}

	public void setCreditorCountry(String creditorCountry) {
		this.creditorCountry = creditorCountry;
	}

	public String getCreditorBankBic() {
		return creditorBankBic;
	}

	public void setCreditorBankBic(String creditorBankBic) {
		this.creditorBankBic = creditorBankBic;
	}

}
