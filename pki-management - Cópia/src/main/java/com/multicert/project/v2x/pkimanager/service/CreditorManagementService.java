/**
 * 
 */
package com.multicert.project.v2x.pkimanager.service;

import java.util.List;

import com.multicert.project.v2x.pkimanager.model.CreditorConf;

/**
 * @author ccardoso
 *
 */
public interface CreditorManagementService {

	
	public CreditorConf getCreditorConfByCreditorId(String creditorId);
	
	public void saveOrUpdateCreditorConfData(CreditorConf conf);
	
	public List<CreditorConf> getAllCreditorsConf();
	
	public void deleteCreditor(String creditorId);
	
}
