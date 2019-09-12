package com.multicert.project.v2x.pkimanager.service;

import java.io.IOException;
import java.util.List;

import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.CAresponse;
import com.multicert.project.v2x.pkimanager.model.ConfigResponse;
import com.multicert.project.v2x.pkimanager.model.Request;
import com.multicert.project.v2x.pkimanager.model.Response;
import com.multicert.project.v2x.pkimanager.model.Vehicle;
import com.multicert.project.v2x.pkimanager.model.VehiclePojo;
import com.multicert.v2x.cryptography.BadContentTypeException;
import com.multicert.v2x.cryptography.UnknownItsException;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;

public interface RAService {

	public void saveRequest(Request request);
	
	public Request getRequest(Long requestId);
	
	/**
	 * Method that verifies if the vehicle is configured within the RA and if the CA is ready to process an certificate request.
	 * If the both conditions are met, the request if forwarded to the enrollment CA
	 * @param request, the vehicle's request for enrollment or authorization
	 * @param type, the type of request: true for enrollment, false for authorizationS
	 * @return 
	 */
	public CAresponse verifySource(Request request, boolean type) throws Exception;

	public Vehicle getVehicle(String vehicleName);
	
	public Vehicle saveVehicle(VehiclePojo vehicle) throws BadContentTypeException;

	/**
	 * Method that generates a response to vehicle enrollment/authorization requests
	 * @param ecRequest the original request
	 * @param encodedResponse the encoded response
	 * @param responseMessage a message to the vehicle 
	 * @param isSuccess if the CA was able to encode a response 
	 * @return the enrollment certificate or an error code
	 * @throws Exception 
	 */
	Response genEnrollResponseDTO(Request ecRequest, byte[] encodedResponse, String responseMessage, Boolean isSuccess) throws Exception;
	
	Response getResponse(long responseId);
	/**
	 * Method that generates and saves a response to vehicle configuration
	 * @para, the name of the RA that will respond to the vehicle
	 * @param vehicle the destination vehicle
	 * @param isSuccess if the configuration of the vehicle was successful or not
	 * @param resposneMessage a response message
	 * @return returns the trusted Ca certificates or error code
	 * @throws IOException 
	 * @throws Exception 
	 */
	ConfigResponse genConfigResponse(String RAname, VehiclePojo vehicleP, boolean isSuccess, String resposneMessage)
			throws IOException, Exception;

	Response genAuthResponseDTO(Request ecRequest, byte[] encodedResponse, String resposneMessage, Boolean isSuccess);
	
}
