/**
 * 
 */
package com.multicert.project.v2x.pkimanager.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multicert.project.v2x.pkimanager.model.DebtorBank;
import com.multicert.project.v2x.pkimanager.model.User;
import com.multicert.project.v2x.pkimanager.repository.DebtorBankRepository;
import com.multicert.project.v2x.pkimanager.repository.UserRepository;

/**
 * @author ccardoso
 *
 */
@Service("debtorManagementService")
public class DebtorManagementServiceImpl implements DebtorManagementService {


	@Autowired
	private DebtorBankRepository debtorBankRepository;

	@Autowired
	private UserRepository userRepository;


	public int saveOrUpdateDebtorBank(DebtorBank bank){
		return debtorBankRepository.save(bank).getBankId();		
	}

	public List<DebtorBank> gellAllDebtorBanks(){
		return debtorBankRepository.findAll();
	}

	public void addDebtorBank(Long userId, DebtorBank bank){

		int	bankId = this.saveOrUpdateDebtorBank(bank);

		User u = userRepository.getOne(userId);

		if(u.getDebtors() == null){
			u.setDebtors(new HashSet<DebtorBank>(1));
		}
		u.getDebtors().add(debtorBankRepository.getOne(bankId));

		userRepository.save(u);
	}


}
