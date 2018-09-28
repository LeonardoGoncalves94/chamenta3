/**
 * 
 */
package com.multicert.project.v2x.pkimanager.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author ccardoso
 *
 */
@Entity
@Table(name = "debtor_bank")
public class DebtorBank {
	

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="bank_id")
	private int bankId;
	
	@Column(name="bank_bic", unique=true)
	@NotEmpty
	private String bankBic;
	
	@Column(name="bank_name")
	@NotEmpty
	private String bankName;
	
	@Column(name="approval_type")
	@NotEmpty
	private String approvalType;
	
	public int getBankId() {
		return bankId;
	}

	public void setBankId(int bankId) {
		this.bankId = bankId;
	}

	public String getBankBic() {
		return bankBic;
	}

	public void setBankBic(String bankBic) {
		this.bankBic = bankBic;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getApprovalType() {
		return approvalType;
	}

	public void setApprovalType(String approvalType) {
		this.approvalType = approvalType;
	}

}
