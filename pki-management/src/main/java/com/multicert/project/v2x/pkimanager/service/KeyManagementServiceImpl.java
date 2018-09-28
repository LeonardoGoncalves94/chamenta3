package com.multicert.project.v2x.pkimanager.service;

import java.security.KeyPair;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multicert.project.v2x.pkimanager.DemoApplication;
import com.multicert.project.v2x.pkimanager.JavaKeyStore;
import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.Key;
import com.multicert.project.v2x.pkimanager.repository.KeyRepository;
import com.multicert.v2x.datastructures.base.Signature;


@Service("KeyManagementService")

public class KeyManagementServiceImpl implements KeyManagementService {

	@Autowired
	KeyRepository keyRepository;
	@Autowired
	V2XService v2xService;
	
	private void saveOrUpdateKeyData(Key key) {
		
		Key storedKey = this.getKeyById(key.getKeyId());

		if(storedKey != null){
			storedKey.setAlias(key.getAlias());
		}else {
			storedKey = key;
		}

		keyRepository.save(storedKey);
	}

	@Override
	public Key getKeyById(Long keyId) {
		return keyRepository.findBykeyId(keyId);
	}

	@Override
	public List<Key> getAllKeys() {
		return keyRepository.findAll();
	}

	@Override
	public void deleteKey(Long keyId) {
		keyRepository.delete(keyId);	
	}
	
	@Override
	public void changeKey(Key key) throws Exception{	
	// chama codigo para mudar o alias da key
	}
	
	
	@Override
	public void saveKey(Key key) throws Exception{
		//generate the key pair
		KeyPair keypair = v2xService.genKeyPair(key.getAlgorithm());
		
		//save the key pair in the keystore
		JavaKeyStore.addKeyPair(keypair, key.getAlias());
		JavaKeyStore.printKestore();
		
		//save the key pair data on the database
		saveOrUpdateKeyData(key);
	}
	


}
