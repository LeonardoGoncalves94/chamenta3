package com.multicert.project.v2x.pkimanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.Key;


@Repository("KeyRepository")
public interface KeyRepository extends JpaRepository<Key, Long>{
	
	public Key findBykeyId(Long keyId);
		
}
