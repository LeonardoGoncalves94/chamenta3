package com.multicert.project.v2x.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.datastructures.base.Signature;

public class VehicleGenerator {
	
	private Map<String, Vehicle> vehicles = new HashMap<String, Vehicle>();
	private AlgorithmType vehicleAlg = Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE; //default vehicle algorithm 
	private int numberOfVehicles; 
	private V2X v2x;
	private File inputFile;
	
	/**
	 * Constructor used for generating vehicles with the default public key algorithm
	 * @param numberOfVehicles number of vehicles that will be generated
	 * @param v2x an object of the v2x package 
	 */
	public VehicleGenerator() throws Exception
	{
		this.v2x = new V2XImpl();
		
		URL fileUrl = getClass().getResource("/config.properties");
	    inputFile = new File(fileUrl.getFile());
		try (InputStream input = new FileInputStream(inputFile)) {
			Properties prop = new Properties();
			// load a properties file
			prop.load(input);
			
			String numberOfVehiclesStr = prop.getProperty("number.vehicles");
			numberOfVehicles = Integer.parseInt(numberOfVehiclesStr);
			
				
			

		} catch (IOException ex) {
			ex.printStackTrace();
	    }
				

		
	}
	
	/**
	 * Constructor used for generating vehicles with a specific public key algorithm
	 * @param numberOfVehicles  number of vehicles that will be generated
	 * @param vehicleAlg the algorithm to be used for generating each vehicle's keys (use an item of Signature.SignatureTypes)
	 * @param v2x an object of the v2x package 
	 */
	public VehicleGenerator(int numberOfVehicles, AlgorithmType vehicleAlg) throws Exception
	{
		this.numberOfVehicles = numberOfVehicles;
		this.vehicleAlg = vehicleAlg;
		this.v2x = new V2XImpl();

		
	}
	
	public void init() throws Exception
	{
	
		RandomStringGenerator gen = new RandomStringGenerator(9); 
		
		for(int i = 0; i < numberOfVehicles; i ++) 
		{
			String itsId = gen.nextString();
			KeyPair itscanonicalPair = v2x.genKeyPair(vehicleAlg);
			
			//get vehicle properties
			int aTNumber = 0;
			int aTUsage = 0;
			boolean reuseAt = false;
			
			try (InputStream input = new FileInputStream(inputFile)) {
					Properties prop = new Properties();
				
					prop.load(input);
					
					String aTNumberStr = prop.getProperty("authorization.ticket.number");
					String aTUsageStr = prop.getProperty("max.authorization.ticket.usage");
					String ReuseATStr = prop.getProperty("reuse.authorization.ticket");
					
					aTNumber = Integer.parseInt(aTNumberStr);
					aTUsage = Integer.parseInt(aTUsageStr);			
					reuseAt = Boolean.parseBoolean(ReuseATStr);

				} catch (IOException ex) {
					ex.printStackTrace();
			    }
				
			
			Vehicle vehicle = new Vehicle(itsId,itscanonicalPair,vehicleAlg,aTNumber,aTUsage,reuseAt);
			if(i == 0) {
				vehicle.setSpecial(true);
			}
			vehicles.put(itsId, vehicle);
		}
	}

	public Map<String, Vehicle> getVehicles() {
		return vehicles;
	}
	


}
