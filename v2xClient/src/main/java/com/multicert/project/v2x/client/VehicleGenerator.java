package com.multicert.project.v2x.client;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;


import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.datastructures.base.Signature;

public class VehicleGenerator {
	
	private Map<String, Vehicle> vehicles = new HashMap<String, Vehicle>();
	private AlgorithmType vehicleAlg = Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE; //default vehicle algorithm TODO: add some randomness in the algorithm choice for each vehicle
	private int numberOfVehicles; 
	private static V2X v2x;
	
	/**
	 * Constructor used for generating vehicles with the default public key algorithm
	 * @param numberOfVehicles number of vehicles that will be generated
	 * @param v2x an object of the v2x package 
	 */
	public VehicleGenerator(int numberOfVehicles, V2X v2x) throws Exception
	{
		this.numberOfVehicles = numberOfVehicles;
		this.v2x = v2x;
		
	}
	
	/**
	 * Constructor used for generating vehicles with a specific public key algorithm
	 * @param numberOfVehicles  number of vehicles that will be generated
	 * @param vehicleAlg the algorithm to be used for generating each vehicle's keys (use an item of Signature.SignatureTypes)
	 * @param v2x an object of the v2x package 
	 */
	public VehicleGenerator(int numberOfVehicles, AlgorithmType vehicleAlg, V2X v2x) throws Exception
	{
		this.numberOfVehicles = numberOfVehicles;
		this.vehicleAlg = vehicleAlg;
		this.v2x = v2x;
		
	}
	
	public void init() throws Exception
	{
	
		RandomStringGenerator gen = new RandomStringGenerator(9); 
		
		for(int i = 0; i < numberOfVehicles; i ++) 
		{
			String itsId = gen.nextString();
			KeyPair itscanonicalPair = v2x.genKeyPair(vehicleAlg);
			
			vehicles.put(itsId, new Vehicle(itsId,itscanonicalPair,vehicleAlg, v2x));
		}
	}

	public Map<String, Vehicle> getVehicles() {
		return vehicles;
	}
	


}
