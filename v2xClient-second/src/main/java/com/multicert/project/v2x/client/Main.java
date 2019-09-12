package com.multicert.project.v2x.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class Main {
	
	public static V2X v2x; //an interface to the v2x package
	
	public static void main(String[] args) throws Exception {
		
		
		VehicleGenerator vg = new VehicleGenerator(2);
		vg.init();
		Map<String, Vehicle> vehicles = vg.getVehicles();

		List <Vehicle> cars = new ArrayList<>(vehicles.values());
		for (Entry<String, Vehicle> entry : vehicles.entrySet())
		{
			List <Vehicle> nerbyVehicles = new ArrayList<>(vehicles.values()); //put the nearby vehicles in a list
			entry.getValue().refreshNearbyVehicles(nerbyVehicles);
			Thread.sleep(1000);
			entry.getValue().configureVehicle();
			Thread.sleep(1000);
			entry.getValue().enrollVehicle();
			Thread.sleep(1000);
			entry.getValue().authorizeVehicle();
			Thread.sleep(1000);
			cars.add(entry.getValue());
		}
		while(true) {
			for (Vehicle car : cars) {
				car.calculateNextAuthorizationTicket();
				car.sendCAM();
				Thread.sleep(1000);

			}
		}
		
		
	}
	
	

}
