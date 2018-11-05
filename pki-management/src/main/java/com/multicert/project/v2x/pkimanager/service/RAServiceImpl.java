package com.multicert.project.v2x.pkimanager.service;


import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.ConfigResponse;
import com.multicert.project.v2x.pkimanager.model.Request;
import com.multicert.project.v2x.pkimanager.model.Response;
import com.multicert.project.v2x.pkimanager.model.Vehicle;
import com.multicert.project.v2x.pkimanager.model.VehiclePojo;
import com.multicert.project.v2x.pkimanager.repository.RequestRepository;
import com.multicert.project.v2x.pkimanager.repository.ResponseRepository;
import com.multicert.project.v2x.pkimanager.repository.VehicleRepository;
import com.multicert.project.v2x.pkimanager.repository.ConfigResponseRepository;
import com.multicert.v2x.cryptography.BadContentTypeException;
import com.multicert.v2x.cryptography.IncorrectRecipientException;
import com.multicert.v2x.cryptography.UnknownItsException;
import com.multicert.v2x.datastructures.base.PublicVerificationKey;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.certificate.SequenceOfCertificate;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;

@Service("raService")
public class RAServiceImpl implements RAService {

	@Autowired
	private RequestRepository requestRepository;
	@Autowired
	private VehicleRepository vehicleRepository;
	@Autowired
	private ResponseRepository responseRepository;
	@Autowired
	private ConfigResponseRepository configResponseRepository;
	@Autowired
	private CaService caService;
	@Autowired
	private V2XService v2xService;
	


	@Override
	public void saveRequest(Request ecRequest) {
		requestRepository.save(ecRequest);	
		//TODO requests are saved on the BD but also need to be deleted, when vehicle confirms its delivery

	}

	@Override
	public Request getRequest(Long requestId) {
		return requestRepository.findByrequestId(requestId);
	}

	@Override
	public EtsiTs103097Data verifySource(Request request, boolean type, boolean requestVerification) throws Exception{
		
		String originName = request.getRequestOrigin();
		Vehicle origin = getVehicle(originName);
		if(origin == null) 
		{
			throw new UnknownItsException ("Error validating EcRequest: the specified origin is not a known ITS station");
		}		
		PublicKey canonicalKey = origin.getPublicKey(); //get the origin vehicle's public key
		
		//get the reference the the vhielce's profile
		String profileName = origin.getProfileName();
		
		String destination = request.getRequestDestination();
		if(!caService.isReady(destination))
		{	
			throw new IncorrectRecipientException ("Error validating EcRequest: Recipient CA does not exist");
		}
		
		String stringRequest = request.getRequestEncoded();
		byte[] encodedRequest = decodeHex(stringRequest);
		
		if(type) //if it is and enrollment request
		{
			System.out.println(profileName +";"+ destination);
			return caService.validateEcRequest(encodedRequest, profileName, canonicalKey, destination); // send profile info here
			
		}
		else // if it is authorization request
		{
			return caService.validateAtRequest(encodedRequest, profileName, destination, requestVerification); // send profile info here
		}
		
		
	}
	

	
	@Override
	public Vehicle getVehicle(String vehicleName) {
		return vehicleRepository.findByvehicleId(vehicleName);	
	}
	
	
	@Override
	public Vehicle saveVehicle(VehiclePojo vehicleP) throws BadContentTypeException {
		Vehicle storedVehicle = this.getVehicle(vehicleP.getVehicleId());
		if(storedVehicle != null)
		{
			return storedVehicle;
		}
		
		Vehicle vehicle = pojoToVehicle(vehicleP);
		
		return vehicleRepository.save(vehicle);
	
	}
	
	/**
	 * Help method that transforms a VehiclePojo into the database object Vehicle
	 * @throws BadContentTypeException 
	 */
	private Vehicle pojoToVehicle(VehiclePojo vehicleP) throws BadContentTypeException
	{
		try {
			String stringVerificationKey = vehicleP.getPublicKey();
			byte[] encodedVerificationKey = decodeHex(stringVerificationKey); //String public verification key to encoded encoded public verification key
		
		
			PublicVerificationKey decodedVerificationKey = new PublicVerificationKey(encodedVerificationKey); //decode the public verification key
		
			PublicKey canonicalKey = v2xService.extractPublicKey(decodedVerificationKey);
			String vehicleId = vehicleP.getVehicleId();
			
			String profileName = toProfileName(vehicleP.getVehicleType()); //get a reference to the vehicle's profile 
			
			return new Vehicle(vehicleId,canonicalKey, profileName);
					
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadContentTypeException("Error decoding canonical public key");
		}	
		
	}
	
	/**
	 * Help method that converts a known vehicle type into a reference to the corresponding profile
	 * @return
	 */
	private String toProfileName(int vehicleType) 
	{
		switch(vehicleType)
		{		
			default:
				return "profile1";
		}
	}
	//TODO SAVE IT
	/**
	 * Help method that encodes bytes into string
	 * @param bytes
	 * @return
	 */
	private String encodeHex(byte[] bytes) 
	{
		return Hex.toHexString(bytes);
	}
	
	/**
	 * Help method that encodes bytes into string
	 * @param bytes
	 * @return
	 */
	private byte[] decodeHex(String string)
	{
		return Hex.decode(string);
	}
	
	@Override
	public Response genResponse(Request ecRequest, byte[] encodedResponse, String resposneMessage, Boolean isSuccess)
	{	
		Response ecResponse = null;
		
		if(isSuccess) //if the EA was able to build an enrollment response
		{
			String stringResponse = encodeHex(encodedResponse);
			ecResponse =  new Response(ecRequest.getRequestDestination(), ecRequest.getRequestOrigin(), resposneMessage, stringResponse, isSuccess);
		}
		else
		{
			ecResponse =  new Response(ecRequest.getRequestDestination(), ecRequest.getRequestOrigin(), resposneMessage, "", isSuccess);

		}
		
		//saveResponse(ecResponse); 
		return ecResponse;
	}
	
	
	@Override
	public ConfigResponse genConfigResponse(String RAname, VehiclePojo vehicleP, boolean isSuccess, String resposneMessage) throws IOException
	{
		ConfigResponse confResponse = null;
		
		if(isSuccess)
		{
			CA rootCA = caService.getRoot();
			List<CA> trustedAA = caService.getValidSubCas("Authorization");
			List<CA> trustedEA = caService.getValidSubCas("Enrollment");
			
			if(rootCA == null || trustedAA.isEmpty() || trustedEA.isEmpty())
			{
				return new ConfigResponse(RAname,vehicleP.getVehicleId(), false, "Something went wrong during vehicle configuration, try again later ", null, null, null, null);
			}
			
			CA ea = selectEA(trustedEA);
			CA aa = selectAA(trustedAA);
			
			String RootCert = encodeHex(rootCA.getCertificate().getEncoded());

			List <EtsiTs103097Certificate> aux = new ArrayList<EtsiTs103097Certificate>(); //list that will contain the trusted AA certificates in the Etsi 103 097 format
			for(CA ca : trustedAA)
			{
				aux.add(new EtsiTs103097Certificate( ca.getCertificate().getEncoded()));
			}
			String trustedAACerts = encodeHex(new SequenceOfCertificate(aux).getEncoded());
			
			String eaCert = encodeHex(ea.getCertificate().getEncoded());
			
			String aaCert = encodeHex(aa.getCertificate().getEncoded());
			
			
			confResponse = new ConfigResponse(RAname,vehicleP.getVehicleId(), isSuccess, resposneMessage,RootCert, trustedAACerts, eaCert, aaCert);
		
			
		}
		else
		{
			confResponse = new ConfigResponse(RAname,vehicleP.getVehicleId(), isSuccess, resposneMessage, null, null, null, null);
			
		}
		
		//saveConfResponse(confResponse); // save in the database wait to vehicle ack then delete
		return confResponse;
	}
	
	/**
	 * Help method that selects a random valid enrollment authority to serve a vehicle. 
	 */
	private CA selectEA(List<CA> trustedEa)
	{
		Random rand = new Random();
		return trustedEa.get(rand.nextInt(trustedEa.size()));
	}
	
	/**
	 * Help method that selects a random valid  authorization authority  to serve a vehicle
	 */
	private CA selectAA(List<CA> trsutedAa)
	{
		Random rand = new Random();
		return trsutedAa.get(rand.nextInt(trsutedAa.size()));
	}

	 
	private void saveResponse(Response ecResponse) {
		responseRepository.save(ecResponse);	
		//TODO responses are saved on the BD but also need to be deleted, when vehicle confirms its delivery
	}
	
	private void saveConfResponse(ConfigResponse configResponse) {
		configResponseRepository.save(configResponse);	
		//TODO responses are saved on the BD but also need to be deleted, when vehicle confirms its delivery
	}
	
	@Override
	public Response getResponse(long responseId) {
		return responseRepository.findByresponseId(responseId);
	}
		
}
