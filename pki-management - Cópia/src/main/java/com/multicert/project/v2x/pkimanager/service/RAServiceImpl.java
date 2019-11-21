package com.multicert.project.v2x.pkimanager.service;


import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.CAresponse;
import com.multicert.project.v2x.pkimanager.model.ConfigResponse;
import com.multicert.project.v2x.pkimanager.model.Profile;
import com.multicert.project.v2x.pkimanager.model.Request;
import com.multicert.project.v2x.pkimanager.model.Response;
import com.multicert.project.v2x.pkimanager.model.Vehicle;
import com.multicert.project.v2x.pkimanager.model.VehiclePojo;
import com.multicert.project.v2x.pkimanager.repository.RequestRepository;
import com.multicert.project.v2x.pkimanager.repository.ResponseRepository;
import com.multicert.project.v2x.pkimanager.repository.VehicleProfileRepository;
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
	private VehicleProfileRepository vehicleProfileRepository;
	@Autowired
	private CaService caService;
	@Autowired
	private V2XService v2xService;
	
	@Value("${authorization.test.on}")
	private Boolean testMode;
	
	@Value("${verify.vehicle.enrollment.on.auth.request}")
	 private Boolean verifyEnrollment;
	 
	
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
	public CAresponse verifySource(Request request, boolean type) throws Exception{
		

		String originName = request.getRequestOrigin();
		Vehicle origin = getVehicle(originName);
		if(origin == null) 
		{
			throw new UnknownItsException ("Error validating EcRequest: the specified origin is not a known ITS station");
		}		
		PublicKey canonicalKey = origin.getPublicKey(); //get the origin vehicle's public key
		
		//get the reference the the vehicle's profile
		String profileName = origin.getProfile().getProfileName();
		
		String destination = request.getRequestDestination();
		if(!caService.isReady(destination))
		{	
			throw new IncorrectRecipientException ("Error validating EcRequest: Recipient CA does not exist");
		}
		
		String stringRequest = request.getRequestEncoded();
		byte[] encodedRequest = decodeHex(stringRequest);
		
		if(type) //if it is and enrollment request
		{
			return caService.validateEcRequest(encodedRequest, profileName, canonicalKey, destination); // send profile info here
			
		}
		else // if it is authorization request
		{
			
			return caService.validateAtRequest(encodedRequest, profileName, destination, validateAuthorizationRequestNumber(request)); // send profile info here
		}
		
		
	}
	/**
	 * Method that validates if the AT request is the first for the vehicle's AT process
	 * @return true if the vehicles Enrollment cert needs to be verifies by an EA, otherwise false 

	 */
	public Boolean validateAuthorizationRequestNumber(Request request) {
		if(testMode) {
			if(verifyEnrollment) {
				return true;
			}else {
				return false;
			}
		}
		
		Vehicle vehice = getVehicle(request.getRequestOrigin());
		Profile vehicleProfile = vehice.getProfile();
		int allowedRequests = vehicleProfile.getNumberOfATs();
		int doneRequests = vehice.getaTCount();
		
		if(doneRequests == 0) {
			return true;
		}else {
			return false;
		}
		
		
	}
	
	@Override
	public Vehicle getVehicle(String vehicleName) {
		return vehicleRepository.findByvehicleId(vehicleName);	
	}
	
	
	@Override
	public Vehicle saveVehicle(VehiclePojo vehicleP) throws BadContentTypeException {
		Vehicle storedVehicle = this.getVehicle(vehicleP.getVehicleId());
		//if(storedVehicle != null)
		//{
			//return storedVehicle;
		//}
		
		Vehicle vehicle = pojoToVehicle(vehicleP);
		
		return vehicleRepository.save(vehicle);
	
	}
	
	/**
	 * Help method that transforms a VehiclePojo into the database object Vehicle
	 * @throws BadContentTypeException z
	 */
	private Vehicle pojoToVehicle(VehiclePojo vehicleP) throws BadContentTypeException
	{
		try {
			String stringVerificationKey = vehicleP.getPublicKey();
			byte[] encodedVerificationKey = decodeHex(stringVerificationKey); //String public verification key to encoded encoded public verification key
		
		
			PublicVerificationKey decodedVerificationKey = new PublicVerificationKey(encodedVerificationKey); //decode the public verification key
		
			PublicKey canonicalKey = v2xService.extractPublicKey(decodedVerificationKey);
			String vehicleId = vehicleP.getVehicleId();
			
			//get a reference to the vehicle's profile
			Profile vehicleProfile = vehicleProfileRepository.findByVehicleType(vehicleP.getVehicleType());
			
			if(vehicleProfile == null) {
				vehicleProfile = vehicleProfileRepository.findByProfileName("profile1");
			}
			
			return new Vehicle(vehicleId,canonicalKey, vehicleProfile);
					
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadContentTypeException("Error decoding canonical public key");
		}	
		
	}

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
	 * Help method that decodes a string into bytes
	 * @param bytes
	 * @return
	 */
	private byte[] decodeHex(String string)
	{
		return Hex.decode(string);
	}
	
	@Override
	public Response genEnrollResponseDTO(Request ecRequest, byte[] encodedResponse, String resposneMessage, Boolean isSuccess)
	{	
		Response ecResponse = null;
		Vehicle vehicle = vehicleRepository.findByvehicleId(ecRequest.getRequestOrigin());
		int numberOfAuthorizationRequests = vehicle.getProfile().getNumberOfATs();
		
		if(isSuccess) //if the EA was able to build an enrollment response
		{
			String stringResponse = encodeHex(encodedResponse);
			ecResponse =  new Response(numberOfAuthorizationRequests,ecRequest.getRequestDestination(), ecRequest.getRequestOrigin(), resposneMessage, stringResponse, isSuccess);
		}
		else
		{
			ecResponse =  new Response(ecRequest.getRequestDestination(), ecRequest.getRequestOrigin(), resposneMessage, "", isSuccess);

		}
		
		//saveResponse(ecResponse); 
		return ecResponse;
	}
	
	@Override
	public Response genAuthResponseDTO(Request ecRequest, byte[] encodedResponse, String resposneMessage, Boolean isSuccess)
	{	
		Response ecResponse = null;
		Vehicle vehicle = vehicleRepository.findByvehicleId(ecRequest.getRequestOrigin());
		int numberOfAuthorizationRequests = vehicle.getProfile().getNumberOfATs();
		
		if(isSuccess) //if the authorization request is successfull (the response contains a valid AT)
		{
			String stringResponse = encodeHex(encodedResponse);
			ecResponse =  new Response(numberOfAuthorizationRequests,ecRequest.getRequestDestination(), ecRequest.getRequestOrigin(), resposneMessage, stringResponse, isSuccess);
			vehicle.setaTCount(vehicle.getaTCount() + 1);
			vehicleRepository.save(vehicle);
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
