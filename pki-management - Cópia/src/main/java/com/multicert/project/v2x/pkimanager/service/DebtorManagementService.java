/**
 * 
 */
package com.multicert.project.v2x.pkimanager.service;

import java.util.List;

import com.multicert.project.v2x.pkimanager.model.DebtorBank;

/**
 * @author ccardoso
 *
 */
public interface DebtorManagementService {
	
	public int saveOrUpdateDebtorBank(DebtorBank bank);
	
	public List<DebtorBank> gellAllDebtorBanks();
	
	public void addDebtorBank(Long userId, DebtorBank bank);
	

}
