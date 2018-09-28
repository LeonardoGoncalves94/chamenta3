package com.multicert.project.v2x.pkimanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.multicert.project.v2x.pkimanager.model.Vehicle;

@Repository("vehicleRepository")
public interface VehicleRepository extends JpaRepository<Vehicle, String>{

	public Vehicle findByvehicleId(String vehicleId);
}
