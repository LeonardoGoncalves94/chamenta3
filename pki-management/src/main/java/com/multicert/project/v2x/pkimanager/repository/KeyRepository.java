package com.multicert.project.v2x.pkimanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multicert.project.v2x.pkimanager.model.Key;


@Repository("KeyRepository")
public interface KeyRepository extends JpaRepository<Key, Long>{
	
	public Key findBykeyId(Long keyId);
}
