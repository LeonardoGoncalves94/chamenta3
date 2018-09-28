package com.multicert.project.v2x.pkimanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multicert.project.v2x.pkimanager.model.CreditorConf;


@Repository("creditorConfRepository")
public interface CreditorConfRepository extends JpaRepository<CreditorConf, String>{

	CreditorConf findByCreditorId(String creditorId);
}
