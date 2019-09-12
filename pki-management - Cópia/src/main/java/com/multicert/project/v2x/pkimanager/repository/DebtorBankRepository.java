/**
 * 
 */
package com.multicert.project.v2x.pkimanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multicert.project.v2x.pkimanager.model.DebtorBank;

/**
 * @author ccardoso
 *
 */
@Repository("debtorBankRepository")
public interface DebtorBankRepository extends JpaRepository<DebtorBank, Integer> {
	
	DebtorBank findByBankBic(String bankBic);

}
