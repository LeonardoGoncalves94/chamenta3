package com.multicert.project.v2x.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Main {
	
	public static V2X v2x; //an interface to the v2x package
	
	public static void main(String[] args) throws Exception {
		v2x = new V2XImpl();
		
		VehicleGenerator vg = new VehicleGenerator(2, v2x);
		vg.init();
		
		Map<String, Vehicle> vehicles = vg.getVehicles();
		
		
		for (Entry<String, Vehicle> entry : vehicles.entrySet())
		{
			entry.getValue().configureVehicle();
			entry.getValue().enrollVehicle();
			entry.getValue().authorizeVehicle();
			

			List <Vehicle> nerbyVehicles = new ArrayList<Vehicle>(vehicles.values()); //put the nerby vehicles in a list
			entry.getValue().refreshNearbyVehicles(nerbyVehicles);
		}
		
		for (Entry<String, Vehicle> entry : vehicles.entrySet())
		{
			entry.getValue().sendCAM();
		}
		
	}
	
	

}
