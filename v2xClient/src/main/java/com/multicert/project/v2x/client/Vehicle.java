package com.multicert.project.v2x.client;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.SecretKey;

import org.bouncycastle.util.encoders.Hex;

import com.multicert.project.v2x.client.api.RaControllerApi;
import com.multicert.project.v2x.client.model.ConfigResponse;
import com.multicert.project.v2x.client.model.Request;
import com.multicert.project.v2x.client.model.Response;
import com.multicert.project.v2x.client.model.VehiclePojo;
import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.cryptography.BadContentTypeException;
import com.multicert.v2x.datastructures.base.HashedId3;
import com.multicert.v2x.datastructures.base.HashedId8;
import com.multicert.v2x.datastructures.base.PublicVerificationKey;
import com.multicert.v2x.datastructures.base.SymmAlgorithm;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.certificate.SequenceOfCertificate;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.AuthorizationResponseCode;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.InnerAtResponse;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.EnrollmentResonseCode;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.InnerEcResponse;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;
import com.multicert.v2x.datastructures.message.secureddata.SignerIdentifier;

public class Vehicle {
	
	private String itsId; //name of this vehicle
	private KeyPair canonicalPair = null; //keypair provided at manufacture to id this vehicle
	private EtsiTs103097Certificate trustAnchor; //the certificate of the root CA
	private Map<HashedId8, EtsiTs103097Certificate> trustStore = new HashMap<HashedId8, EtsiTs103097Certificate>(); //The collection of trusted AA certificates
	private EtsiTs103097Certificate eaCert; //the certificate of the EA that will enroll this vehicle
	private EtsiTs103097Certificate aaCert; //the certificate of the AA that will authorize this vehicle
	
	private boolean configured = false;
	private boolean enrolled = false;
	
	private KeyPair enrollVerificationKey = null; //new verification keys to be certified by the enrollment credential
	private SecretKey secretKeyEA = null;//a secret key to encrypt the EcRequest and share with the EA
	private EtsiTs103097Certificate enrollmentCredential = null;
	
	Map<byte[], AuthorizationData> AtPool = new HashMap<byte[], AuthorizationData>(); //sub batch of ATs for a week of usage, where key = hashedId8 of the AT; value = AT + sig keypair 
	
	private static final int numberOfATs = 4;
	
	private List <Vehicle> nextVehicles = new ArrayList<Vehicle>();// A list of the nearby vehicles to send v2x messages to
	Map<byte[], EtsiTs103097Certificate> nextAts = new HashMap<byte[], EtsiTs103097Certificate>(); //Store of the nearby vehicle's valid authorization tickets

	
	private AlgorithmType vehicleAlg = null; //Public key algorithm that will be used for this vehicle's cryptographic operations (signature and encryption)
	
	private V2X v2x = null;
	private RaControllerApi raApi = null;
	
	public Vehicle(String itsId, KeyPair canonicalPair, AlgorithmType vehicleAlg, V2X v2x)
	{
		this.itsId = itsId;
		this.canonicalPair = canonicalPair;
		this.vehicleAlg = vehicleAlg;
		this.v2x = v2x;
		raApi = new RaControllerApi();
	}
	/**
	 * This method creates a request for vehicle configuration to the RAService
	 * @throws IllegalArgumentException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	public void configureVehicle() throws IllegalArgumentException, InvalidKeySpecException, IOException, NoSuchAlgorithmException
	{
		PublicVerificationKey PubVerKey = v2x.buildVerificationKey(canonicalPair.getPublic(),vehicleAlg);
		
		VehiclePojo vehicle = new VehiclePojo();
		vehicle.setPublicKey(encodeHex(PubVerKey.getEncoded())); // encoded public verification key to string
		vehicle.setVehicleId(itsId);
		ConfigResponse response = raApi.configureVehicleUsingPOST(vehicle);
		this.configured = response.getIsSuccess();
		
		if(configured)
		{
			trustAnchor = new EtsiTs103097Certificate(decodeHex(response.getTrustAnchor()));
			eaCert = new EtsiTs103097Certificate(decodeHex(response.getEaCert()));
			aaCert = new EtsiTs103097Certificate(decodeHex(response.getAaCert()));
						
			SequenceOfCertificate certSequence = new SequenceOfCertificate(decodeHex(response.getTrustedAA())); // get the sequence of trusted AA certificates
			EtsiTs103097Certificate[] certArray = certSequence.getCerts();
			trustStore = v2x.genTrustStore(certArray); // store the AA certificates in a trustStore
			
			System.out.println("---------- Vehicle configuration result ----------");
			System.out.println();
			System.out.println("Vehicle with id ["+ itsId +"] configured with success!" );
			System.out.println();
			System.out.println("Trust Anchor "+ trustAnchor.toString());
			System.out.println();
			System.out.println("Enrollment Authority "+ eaCert.toString());
			System.out.println();
			System.out.println("Authorization Authoriy "+ aaCert.toString());
			System.out.println();
			System.out.println();
		
		}	
		
	}

	
	public void enrollVehicle() throws GeneralSecurityException, Exception
	{
		if(configured) 
		{
			enrollVerificationKey = v2x.genKeyPair(vehicleAlg); //generate a new verification key pair
			secretKeyEA = v2x.genSecretKey(SymmAlgorithm.AES_128_CCM); //generate a new symmetric key to share with the EA
			EtsiTs103097Data etsiRequest = v2x.genEcRequest(itsId, canonicalPair, enrollVerificationKey,vehicleAlg, eaCert, secretKeyEA); 
			Request request = new Request();
			request.setRequestDestination(eaCert.getName()); 
			request.setRequestOrigin(itsId);
			request.setRequestEncoded(encodeHex(etsiRequest.getEncoded()));
			//request.setRequestEncoded(encodeHex(new byte[] {123})); BADLY FORMED EC REQUEST
	
			Response response = raApi.requestEnrollmentCertUsingPOST(request); //send the request and receive the response
			
			
			if(!response.getIsSuccess()) //If the EA could not build an Enrollment response for this vehicle
			{
				System.out.println("Could not enroll vehicle, " + response.getResponseMessage());
				return;
			}
		
			InnerEcResponse innerResponse = v2x.processEcResponse(decodeHex(response.getRequestEncoded()), eaCert, secretKeyEA);
		
			if(innerResponse == null)
			{
				System.out.println("Enrollment response discarded"); //response could have been tampered, discard the response
				return;
			}
		
			//If the EA denied the enrollment of this vehicle
			if(innerResponse.getResponseCode() != EnrollmentResonseCode.OK)
			{
				System.out.println("Enrollment request denied, response code: " + innerResponse.getResponseCode());
				return;
			}
			enrollmentCredential = innerResponse.getCertificate();
			this.enrolled = true;
			System.out.println("---------- Vehicle enrollment result ["+ itsId +"]----------");
			System.out.println();
			System.out.println("Enrolled with success!" );
			System.out.println();
			System.out.println(enrollmentCredential);
			System.out.println();
			System.out.println();
		}
	}
	
	public void authorizeVehicle() throws Exception
	{
		if(!enrolled)
		{
			System.out.println("Authorization request denied, only enrolled vehicles can start an authorizationr equest");
			return;
		}
		
		Boolean verification = true; //flag this AT request to be verified by the EA
	
			for(int x = 0; x < numberOfATs; x++)
			{
			
				KeyPair verificationKeys = v2x.genKeyPair(vehicleAlg); //generate a new verification key pair
				SecretKey secretKeyAA = v2x.genSecretKey(SymmAlgorithm.AES_128_CCM); //generate a new symmetric key to share with the AA
				SecretKey secretKeyEA = v2x.genSecretKey(SymmAlgorithm.AES_128_CCM); //generate a new symmetric key to share with the EA
				
				EtsiTs103097Data etsiRequest = v2x.genAtRequest(enrollmentCredential, enrollVerificationKey.getPrivate(), vehicleAlg, 
							verificationKeys, aaCert, eaCert, secretKeyAA, secretKeyEA);
				
				Request request = new Request();
				request.setRequestDestination(aaCert.getName()); 
				request.setRequestOrigin(itsId);
				request.setRequestEncoded(encodeHex(etsiRequest.getEncoded()));
		
				Response response = raApi.requestAuthorizationTicketUsingPOST(request, verification);		
					
				if(!response.getIsSuccess()) //If the AA could not build an Authorization response for this request
				{
					System.out.println("Authorization request failed, " + response.getResponseMessage());
					continue;
				}
				
				InnerAtResponse innerResponse = v2x.processAtResponse(decodeHex(response.getRequestEncoded()), aaCert, secretKeyAA);
					
				if(innerResponse == null)
				{
					System.out.println("authorization response discarded"); //response could have been tampered, discard the response
					continue;
				}
					
				AuthorizationResponseCode responseCode = innerResponse.getResponseCode();
				if(responseCode != AuthorizationResponseCode.OK) //If the AA denied the enrollment of this vehicle
				{
					if(responseCode == AuthorizationResponseCode.ENROLLMENT_VERIFICATION_FAILED) //If the provided enrollment credential is not valid
					{
						System.out.println("Authorization request denied, response code: " + innerResponse.getResponseCode());
						break;
					}
						
					System.out.println("Authorization request denied, response code: " + innerResponse.getResponseCode());
					continue;
				}
					
				EtsiTs103097Certificate authTicket = innerResponse.getCertificate();
				byte[] authTicketHash = v2x.genCertificateHashedId(authTicket);
				
				AuthorizationData authData = new AuthorizationData(authTicket,verificationKeys);
					
				AtPool.put(authTicketHash, authData);
				
							
				System.out.println("---------- Vehicle Authorization result ["+ itsId +"]----------");
				System.out.println();
				System.out.println("Vehicle authorized with success!" );
				System.out.println();
				System.out.println("Authorization Ticket ["+ encodeHex(authTicketHash)+"]");
				System.out.println();
				System.out.println();
				verification = false;
			}	
			
					
	}
	
	public void sendCAM() throws SignatureException, NoSuchAlgorithmException, IOException
	{
		System.out.println();
		System.out.println();
		System.out.println("---------- V2X Messages ["+ itsId +"] ----------");
		System.out.println();
	
			for(Entry<byte[], AuthorizationData> entry : AtPool.entrySet())//for each authorization ticket in that week
			{
				String from = this.itsId; // just to print on receiver vehicle
				KeyPair signatureKeys = entry.getValue().getSignatureKeys();
				EtsiTs103097Certificate authTicket = entry.getValue().getAuthorizationTicket();
				
				String payload = "Dymmy payload";
				HashedId3[] requestCerts = new HashedId3[] {};
				
				EtsiTs103097Data cam = v2x.genCAM(authTicket, signatureKeys, vehicleAlg, payload, requestCerts , null, true);
				
				for(Vehicle v : nextVehicles)
				{
					System.out.println("Sending CAM signed with ["+ encodeHex(entry.getKey())+"] to Vehicle ["+ v.getIstId()+"]");
					System.out.println();
					
					v.receiveCAM(cam, from);
				}
				
			}
	}
	
	public void receiveCAM(EtsiTs103097Data cam, String from) 
	{
		System.out.println("Vehicle ["+ itsId +"] Received CAM from ["+ from+"]");
		
			try {
				SignerIdentifier sigId = v2x.getSignerId(cam);
				switch(sigId.getType())
				{
					case CERTIFICATE:
					SequenceOfCertificate cert = (SequenceOfCertificate)sigId.getValue();
					EtsiTs103097Certificate[] authTicket = cert.getCerts();
					EtsiTs103097Certificate ticket = authTicket[0];
					if(v2x.verifyCertificate(ticket, aaCert)) 
					{
						if(v2x.processCAM(cam, ticket))
						{
							System.out.println("CAM verified with success!");
							nextAts.put(v2x.genCertificateHashedId(ticket), ticket);
						}
						else
						{
							System.out.println("Failed to verify the CAM's signature!");
						}
					}
					else
					{
						System.out.println("Failed to verify the vehicle's authorization ticket!");
					}
					break;
					
					case DIGEST:		
					EtsiTs103097Certificate Aticket = nextAts.get(sigId.getValue());
					if(Aticket == null)
					{
						boolean requestAuthTicket = true;
						return;
					}
		
					if(v2x.verifyCertificate(Aticket, aaCert)) //TODO CHANGE TO  ALL TRUSTED AA
						{
							if(v2x.processCAM(cam, Aticket))
							{
								System.out.println("CAM verified with success!");
							
							}
							else
							{
								System.out.println("Failed to verify the CAM's signature!");
							}
						}
						else
						{
							System.out.println("Failed to verify the vehicle's authorization ticket!");
						}
						
				     break;
					default:
					 System.out.println("Could not verify self-signed CAMs");	
				}
			} catch (NoSuchAlgorithmException | IOException | BadContentTypeException e) {
				System.out.println("Could not verify CAM");	
				e.printStackTrace();
			}
	}
	
	public String getIstId()
	{
		return this.itsId;
	}
	
	
	public void refreshNearbyVehicles(List <Vehicle> nextVehicles)
	{
		nextVehicles.remove(this);
		this.nextVehicles = nextVehicles;
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
	 * Help method that encodes bytes into string
	 * @param bytes
	 * @return
	 */
	private byte[] decodeHex(String string)
	{
		return Hex.decode(string);
	}
	
	public String toString()
	{
		PublicKey pubKey = canonicalPair.getPublic();
		 return
	                "vehicle [\n" +
	                        "  id=" + itsId + "\n" +
	                        "  canonicalPubKey=" + Base64.getEncoder().encodeToString(pubKey.getEncoded()) + "\n" +
	                        "  canonicalAlgorithm=" + vehicleAlg.toString() + "\n" +	
	                        "]";
	}
	
}
