package com.multicert.project.v2x.pkimanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.multicert.project.v2x.pkimanager.model.CA;


@Repository("CaRepository")
public interface CaRepository extends JpaRepository<CA, Long>{
	
	public CA findBycaId(Long caID);
	
	public CA findBycaName(String caName);
	
	public List<CA> findBycaGroup(String caGroup);
	
	public List<CA> findBycaType(String caType);
	
	/**
	 * SQL query that returns a list of possible CA subjects for a given CA type. This query is used when creating a certificate to specify the certificate's subject CA
	 * A CA is considered a possible Subject for a given certificate if it already has signature keys associated
	 * @param ca_group, specifies the type of CA we are looking for (RootCa or SubCa)
	 */
	@Query(value = "SELECT * FROM ca INNER JOIN keys ON (ca.ca_id = keys.ca_id ) WHERE (keys.type = 'Signature' AND ca.ca_group = ?1)", nativeQuery = true)
	public List<CA> findSubjects(String ca_group);
	
	/**
	 *SQL query that returns a list of possible CA issuers. Used when creating a certificate to specify the issuer CA
	 */
	@Query(value = "SELECT * FROM ca INNER JOIN keys ON (ca.ca_id = keys.ca_id ) WHERE (keys.type = 'Signature')", nativeQuery = true)
	public List<CA> findIssuers();
	
	@Modifying
	@Query(value = "DELETE FROM ca", nativeQuery = true)
	public void deleteCas();
	
	@Modifying
	@Query(value = "DELETE FROM certificate_regions", nativeQuery = true)
	public void deleteCert_region();
	
	@Modifying
	@Query(value = "DELETE FROM certificate", nativeQuery = true)
	public void deleteCerts();
	
	@Modifying
	@Query(value = "DELETE FROM keys", nativeQuery = true)
	public void deleteKeys();
	
	@Modifying
	@Query(value = "DELETE FROM enrollment_credential", nativeQuery = true)
	public void deleteEnrollCredential();
	
}
