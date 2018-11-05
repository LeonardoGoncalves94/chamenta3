package com.multicert.project.v2x.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Main {
	
	public static V2X v2x; //an interface to the v2x package
	
	public static void main(String[] args) throws Exception {
		
		
		VehicleGenerator vg = new VehicleGenerator(2);
		vg.init();
		
		Map<String, Vehicle> vehicles = vg.getVehicles();
		
		
		for (Entry<String, Vehicle> entry : vehicles.entrySet())
		{
			List <Vehicle> nerbyVehicles = new ArrayList<Vehicle>(vehicles.values()); //put the nearby vehicles in a list
			entry.getValue().refreshNearbyVehicles(nerbyVehicles);
			new Thread(entry.getValue()).start();
			
		}
		
		
	}
	
	

}
