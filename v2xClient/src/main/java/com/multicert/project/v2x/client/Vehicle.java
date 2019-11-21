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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
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

public class Vehicle implements Runnable {
	
	private boolean isSpecial = false;
	// name of this vehicle
	private String itsId; 
	
	// KeyPair provided at manufacture to identify this vehicle
	private KeyPair canonicalPair = null; 
	
	// the certificate of the root CA
	private EtsiTs103097Certificate trustAnchor; 
	
	// The collection of trusted AAs
	private Map<HashedId8, EtsiTs103097Certificate> trustStore = new HashMap<HashedId8, EtsiTs103097Certificate>(); 
	
	// the certificate of the EA that will enroll this vehicle																												// certificates
	private EtsiTs103097Certificate eaCert; 
	
	// the certificate of the AA that will authorize this vehicle
	private EtsiTs103097Certificate aaCert; 

	private boolean configured = false;
	private boolean enrolled = false;
	
	// new verification keys to be certified by the enrollment credential
	private KeyPair enrollVerificationKey = null; 
	
	// a secret key to encrypt the EcRequest and share with the EA
	private SecretKey secretKeyEA = null;
	
	// The enrollment Credential
	private EtsiTs103097Certificate enrollmentCredential = null;
	
	// A pool of ATs to use, where AuthorizationData = AT + sig KeyPair
	private List<AuthorizationData> authorizationTicketPool = new ArrayList<AuthorizationData>();
	
	//if this vehicle will reuse ATs
	private boolean reuseAuthorizationTicket;
	
	// number of ATs on this vehicle's pool
	private Long authorizationTicketNumber; 
	
	 // the index of the current AT to be used to sign a message
	private long authorizationTicketIndex;
	
	// maximum number of message signed by the same AT
	private int maxAuthorizationTicketUsage; 
	
	// current number of messages that will be signed by the same AT
	private int authorizationTicketCounter; 
	
	// A list of the nearby vehicles to send v2x messages
	private List<Vehicle> nextVehicles = new ArrayList<Vehicle>();

	// Store of the nearby vehicle's trusted ATs, where Key = HahsedId8 and value = the AT
	Map<String, EtsiTs103097Certificate> nextAuthorizationTickets = new HashMap<String, EtsiTs103097Certificate>(); 
	
	//if the next CAM will contain the full AT
	private boolean requestAuthorizationTicket = true;

	// A list of CAMs which are pending verification (e.g. vehicle does not know the AT which signed it), where UnknownAuthorzationData = AT + CAM
	private List<UnknownAuthorizationData> pendingCAMs = new ArrayList<UnknownAuthorizationData>(); 
																					
	//Pool of the current unknown ATs, which will be requested in the next CAM	
	private List<HashedId3> unknwonATs = Collections.synchronizedList(new ArrayList<HashedId3>()); 
	
	AuthorizationData currentAuthorizationData = null;
	
	// Public key algorithm that will be used for this vehicle's cryptographic operations (signature and encryption)
	private AlgorithmType vehicleAlg = null; 
										
	// instance of the interface with the v2x lib
	private V2X v2x = null;
	
	// instance to the RA API
	private RaControllerApi raApi = null; 
	
	// This will retrieve line separator independent on OS.
	String newLine = System.getProperty("line.separator");

	final static Logger logger = Logger.getLogger(Vehicle.class);
	
	Timer CAMtimer = new Timer();
	TimerTask task = new TimerTask() {
		public void run() {
			try {
				calculateNextAuthorizationTicket();
				sendCAM();
			} catch (SignatureException | NoSuchAlgorithmException | IOException | BadContentTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Timer includeCertTimer = new Timer();
	TimerTask includeCert = new TimerTask() {
		public void run() {
		
			requestAuthorizationTicket = true;
			
		}
	};

	public Vehicle(String itsId, 
			KeyPair canonicalPair, 
			AlgorithmType vehicleAlg,  
			int maxAuthorizationTicketUsage, 
			boolean reuseAuthorizationTicket) throws Exception {
		this.itsId = itsId;
		this.canonicalPair = canonicalPair;
		this.vehicleAlg = vehicleAlg;
		this.raApi = new RaControllerApi();

		this.maxAuthorizationTicketUsage = maxAuthorizationTicketUsage;
		this.reuseAuthorizationTicket = reuseAuthorizationTicket;
		this.authorizationTicketCounter = maxAuthorizationTicketUsage;
		
		this.v2x = new V2XImpl();
	}

	public void run() {
		try {
			configureVehicle();
			enrollVehicle();
			authorizeVehicle();
			CAMtimer.scheduleAtFixedRate(task, 0, 500);
			includeCertTimer.scheduleAtFixedRate(includeCert, 0, 1000000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method creates a request for vehicle configuration to the RAService
	 * 
	 * @throws IllegalArgumentException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public synchronized void configureVehicle()
			throws IllegalArgumentException, InvalidKeySpecException, IOException, NoSuchAlgorithmException {
		
		PublicVerificationKey PubVerKey = v2x.buildVerificationKey(canonicalPair.getPublic(), vehicleAlg);

		
		VehiclePojo vehicle = new VehiclePojo();
		vehicle.setPublicKey(encodeHex(PubVerKey.getEncoded())); // encoded public verification key to string
		vehicle.setVehicleId(itsId);
		ConfigResponse response = raApi.configureVehicleUsingPOST(vehicle);
		this.configured = response.getIsSuccess();

		if (configured) {

			trustAnchor = new EtsiTs103097Certificate(decodeHex(response.getTrustAnchor()));
			eaCert = new EtsiTs103097Certificate(decodeHex(response.getEaCert()));
			aaCert = new EtsiTs103097Certificate(decodeHex(response.getAaCert()));

			SequenceOfCertificate certSequence = new SequenceOfCertificate(decodeHex(response.getTrustedAA())); // get the sequence of trusted AA certificates
																											
			EtsiTs103097Certificate[] certArray = certSequence.getCerts();
			trustStore = v2x.genTrustStore(certArray); // store the AA certificates in a trustStore
			System.out.println();
			System.out.println("[" + itsId + "] Vehicle configured with success!" + newLine + "EA name: "
					+ eaCert.getName() + " with certificate: " + v2x.genCertificateHashedId3(eaCert) + "" + newLine
					+ "AA name: " + aaCert.getName() + " with certificate: " + v2x.genCertificateHashedId3(aaCert));
			System.out.println();
		} else {
			System.out.println("[" + itsId + "] Could not configure vehicle: Response from RA - " +response.getResponseMessage());
		}

	}

	public synchronized void enrollVehicle() throws GeneralSecurityException, Exception {
		if (!configured) {
			System.out.println(
					"vehicle enrollment request denied, only enrolled vehicles can start the enrollment process");
			return;
		}
		enrollVerificationKey = v2x.genKeyPair(vehicleAlg); // generate a new verification key pair
		secretKeyEA = v2x.genSecretKey(SymmAlgorithm.AES_128_CCM); // generate a new symmetric key to share with the EA
		EtsiTs103097Data etsiRequest = v2x.genEcRequest(itsId, canonicalPair, enrollVerificationKey, vehicleAlg, eaCert,
				secretKeyEA);
		Request request = new Request();
		request.setRequestDestination(eaCert.getName());

		request.setRequestOrigin(itsId);
		request.setRequestEncoded(encodeHex(etsiRequest.getEncoded()));
			
		Response response = raApi.requestEnrollmentCertUsingPOST(request); // send the request and receive the response

		if (!response.getIsSuccess()) // If the EA could not build an Enrollment response for this vehicle
		{
			System.out.println("Could not enroll vehicle, EA " + response.getResponseMessage());
			return;
		}
		//get the number of allowed AT requests from the enrollment request
		authorizationTicketNumber = response.getRequestId();
		this.authorizationTicketIndex = authorizationTicketNumber - 1;

		InnerEcResponse innerResponse = v2x.processEcResponse(decodeHex(response.getRequestEncoded()), eaCert,
				secretKeyEA);

		if (innerResponse == null) {
			System.out.println("Enrollment response discarded"); // response could have been tampered, discard the
																	// response
			return;
		}

		// If the EA denied the enrollment of this vehicle
		if (innerResponse.getResponseCode() != EnrollmentResonseCode.OK) {
			System.out.println("Enrollment request denied, response code: " + innerResponse.getResponseCode());
			return;
		}
		enrollmentCredential = innerResponse.getCertificate();
		this.enrolled = true;
		System.out.println();
		System.out.println("[" + itsId + "] Vehicle enrolled with success!");
		System.out.println();
		System.out.println();

	}

	public synchronized void authorizeVehicle() throws Exception {
		if (!enrolled) {
			System.out.println(
					"Authorization request denied, only enrolled vehicles can start an authorizationr request");
			return;
		}

		Boolean verification = true; // flag this AT request to be verified by the EA

		for (int x = 0; x < authorizationTicketNumber; x++) {

			KeyPair verificationKeys = v2x.genKeyPair(vehicleAlg); // generate a new verification key pair
			SecretKey secretKeyAA = v2x.genSecretKey(SymmAlgorithm.AES_128_CCM); // generate a new symmetric key to
																					// share with the AA
			SecretKey secretKeyEA = v2x.genSecretKey(SymmAlgorithm.AES_128_CCM); // generate a new symmetric key to
																					// share with the EA

			EtsiTs103097Data etsiRequest = v2x.genAtRequest(enrollmentCredential, enrollVerificationKey.getPrivate(),
					vehicleAlg, verificationKeys, aaCert, eaCert, secretKeyAA, secretKeyEA);

			Request request = new Request();
			request.setRequestDestination(aaCert.getName());
			request.setRequestOrigin(itsId);
			request.setRequestEncoded(encodeHex(etsiRequest.getEncoded()));
					
			Response response = raApi.requestAuthorizationTicketUsingPOST(request, verification);

			if (!response.getIsSuccess()) // If the AA could not build an Authorization response for this request
			{
				System.out.println("Authorization request failed, " + response.getResponseMessage());
				continue;
			}

			InnerAtResponse innerResponse = v2x.processAtResponse(decodeHex(response.getRequestEncoded()), aaCert,
					secretKeyAA);

			if (innerResponse == null) {
				
				// response could have been tampered, discard the response
				System.out.println("authorization response discarded"); 
																	
				continue;
			}

			AuthorizationResponseCode responseCode = innerResponse.getResponseCode();
			// If the AA denied the enrollment of this vehicle
			if (responseCode != AuthorizationResponseCode.OK) 
			{
				// If the provided enrollment credential is invalid
				if (responseCode == AuthorizationResponseCode.ENROLLMENT_VERIFICATION_FAILED) 
																					
				{
					System.out.println("Authorization request denied, response code: " + innerResponse.getResponseCode());
					break;
				}

				System.out.println("Authorization request denied, response code: " + innerResponse.getResponseCode());
				continue;
			}

			EtsiTs103097Certificate authTicket = innerResponse.getCertificate();

			AuthorizationData authData = new AuthorizationData(authTicket, verificationKeys);
		
			try {
				authorizationTicketPool.add(authData);
				
			} catch(Exception e) {
				System.out.println(e);
			}

			System.out.println();
			System.out.println("[" + itsId + "] Vehicle authorized with success! " + newLine + "Authorization Ticket: "
					+ v2x.genCertificateHashedId3(authTicket));
			System.out.println();
			System.out.println();
			verification = false;
		}

	}

	/**
	 * Help method that shuffles the authorization tickets order inside this
	 * vehicle's local pool
	 * 
	 * @return the authorization ticket data
	 */
	private void shuffleATs() {
		Collections.shuffle(authorizationTicketPool);
	}

	/**
	 * This method returns the AT that is going to be used next
	 * 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */

	public synchronized void calculateNextAuthorizationTicket() {
		if (authorizationTicketCounter <= 0) {
			authorizationTicketCounter = maxAuthorizationTicketUsage;
			authorizationTicketIndex--;
			if (authorizationTicketIndex < 0) {
				authorizationTicketIndex = authorizationTicketNumber - 1;
				
				if(reuseAuthorizationTicket) {
					shuffleATs();
				}else {
					currentAuthorizationData = null;
					return;
				}
				
			}
		}
		authorizationTicketCounter--;
		currentAuthorizationData = authorizationTicketPool.get((int) authorizationTicketIndex);
	}

	/**
	 * help method that returns true each second
	 * 
	 * @return
	 * 
	 * 		public boolean includeFullAthorizationTicket() {
	 *         if(authorizationTicketCounter % 2 == 0 || requestAuthorizationTicket)
	 *         { requestAuthorizationTicket = false; return true;
	 * 
	 *         } return false; }
	 */

	/**
	 * This method broadcasts a CAM signed with the authorizationData to all nearby
	 * vehicles
	 * 
	 * @param authorizationData, an authorization ticket and the corresponding
	 *        signature key pair
	 * @param includeAt, if this CAM will include the full authorization ticket or
	 *        just the certificate hash
	 * @throws BadContentTypeException 
	 */
	private void sendCAM() throws SignatureException, NoSuchAlgorithmException, IOException, BadContentTypeException {
		//If all certificates have been used, the vehicle will not send more CAMS
				if(currentAuthorizationData == null) {
					CAMtimer.cancel();
					includeCertTimer.cancel();
					return;
				}
		if (isSpecial) {
			System.out.println();
			System.out.println();
			System.out.println("---------- V2X Messages [" + itsId + "] ----------");
			System.out.println();
		}
		// just to print on receiver vehicle
		String from = this.itsId; 
		
		KeyPair signatureKeys = currentAuthorizationData.getSignatureKeys();
		EtsiTs103097Certificate authTicket = currentAuthorizationData.getAuthorizationTicket();
		HashedId3 hashId3 = v2x.genCertificateHashedId3(authTicket);
		String hashId3Str = hashId3.getString();

		String payload = "Dymmy payload";
		HashedId3[] requestCerts = null;
		
		 synchronized(unknwonATs){
			 
			 for(HashedId3 unknownAt : unknwonATs) {
				 logAndPrint("Vehicle [" + itsId + "] Requesting unknown AT: "+ unknownAt +" to peer vehicles ",false);
					
				}
			 	requestCerts = new HashedId3[unknwonATs.size()];
				requestCerts = unknwonATs.toArray(requestCerts);
				
				unknwonATs.removeAll(unknwonATs);
		    }	
		 
		 EtsiTs103097Data cam = v2x.genCAM(authTicket, signatureKeys, vehicleAlg, payload, requestCerts, null, requestAuthorizationTicket);
			
		for (Vehicle v : nextVehicles) {
			//Print on the console and log
			if(requestAuthorizationTicket) {
				logAndPrint("Vehicle [" + itsId + "] Sending CAM ["+hashId3Str+"] signed with full AT to Vehicle [" + v.getIstId() + "]",false);
			}else {
				logAndPrint("Vehicle [" + itsId + "] Sending CAM ["+hashId3Str+"] signed with hashed AT to Vehicle [" + v.getIstId() + "]",false);

			}
					
			v.receiveCAM(cam, from);
		}
		
		requestAuthorizationTicket = false;
	}
	
	public synchronized void receiveCAM(EtsiTs103097Data cam, String from) {
		try {
			
			List<HashedId3> requestedAts = v2x.getRequestedCerts(cam);	
			SignerIdentifier sigId = v2x.getSignerId(cam);
			switch (sigId.getType()) {
			case CERTIFICATE:
				
				SequenceOfCertificate certs = (SequenceOfCertificate) sigId.getValue();
				EtsiTs103097Certificate[] authTicket = certs.getCerts();
				
				EtsiTs103097Certificate ticket = authTicket[0];
				HashedId3 ATHashedId3 = v2x.genCertificateHashedId3(ticket);
				HashedId8 ATHashedId8 = v2x.genCertificateHashedId8(ticket);
				String ATHashedId3Str = ATHashedId3.getString();
				String ATHashedId8Str = ATHashedId8.getString();
		
				logAndPrint("Vehicle [" + itsId + "] Received CAM ["+ATHashedId3Str+"] signed with full AT",true);
			
							
				// look for my AT on the requested ATs within the CAM
				for (HashedId3 hash : requestedAts) {
					if(currentAuthorizationData != null) {
						EtsiTs103097Certificate currentAT = currentAuthorizationData.getAuthorizationTicket();
						HashedId3 currentATHashId3 = v2x.genCertificateHashedId3(currentAT);
						if (java.util.Arrays.equals(currentATHashId3.getData(), hash.getData())) {
							
							logAndPrint("Vehicle ["+itsId+"] Found request current AT [" + currentATHashId3.getString() +"]",false);
			
							requestAuthorizationTicket = true; // will send it next
						}
					}
				}
				
				if (v2x.verifyCertificate(ticket, aaCert)) {
					nextAuthorizationTickets.put(ATHashedId8Str, ticket); // put the hashedId8 in the known ATs		
					
					// verify this CAM
					if (v2x.processCAM(cam, ticket)) {
						logAndPrint("Vehicle ["+itsId+"] Verified CAM [" + ATHashedId3Str + "] with success!",false);

					} else {
						logAndPrint("Vehicle ["+itsId+"] Failed to verify CAM [" + ATHashedId3Str + "]!",false);
						
					}
					
					// try to verify older CAMs signed with currently untrusted ATs
					List<UnknownAuthorizationData> verifiedATs = new ArrayList<UnknownAuthorizationData>();
					for (UnknownAuthorizationData unknownAData : pendingCAMs) {
						if (unknownAData.getAuthorizationTicketAsString().equals(ATHashedId3Str)) { // compare the unknown AT with the trusted AT
							
							// Found one previously untrusted AT that is now trusted
							// verify its CAM
							if (v2x.processCAM(unknownAData.getCam(), ticket)) {
								verifiedATs.add(unknownAData);
								logAndPrint("Vehicle ["+itsId+"] Verified pending CAM ["+ATHashedId3Str +"] with success!",false);

							} else {
								logAndPrint("Vehicle ["+itsId+"] Failed to verify CAM [" + ATHashedId3Str + "]!",false);
								
							}
						}

					}
					pendingCAMs.removeAll(verifiedATs); // remove the verified ATs from the list of unknown ATs
																		
					
				} else {
					// TODO: PEDIR O CERTIFICADO DA CA!!!!!!!!!?
					logAndPrint("Vehicle ["+itsId+"] Failed to verify AT ["+ATHashedId3Str+"]!",false);
					
				}
				break;

			case DIGEST:
				HashedId8 certId8 = (HashedId8) sigId.getValue();
				HashedId3 certId3 = new HashedId3(certId8.getData());
				
				String hashedId8str = certId8.getString();
				String hashedId3str = certId3.getString();
				
				logAndPrint("Vehicle [" + itsId + "] Received CAM with hashed AT [" + hashedId3str + "]",true);
		
				// look for my AT on the requested ATs within the CAM
				for (HashedId3 hash : requestedAts) {
					if(currentAuthorizationData != null) {
						EtsiTs103097Certificate currentAT = currentAuthorizationData.getAuthorizationTicket();
						HashedId3 currentATHashId3 = v2x.genCertificateHashedId3(currentAT);
						if (java.util.Arrays.equals(currentATHashId3.getData(), hash.getData())) {
							logAndPrint("Vehicle [" + itsId + "] Found request for current AT [" + currentATHashId3.getString() +"]",false);
							// will send it next
							requestAuthorizationTicket = true; 
						}
					}
				}
				
				//Look for the signing AT within the list of known ATs
				EtsiTs103097Certificate Aticket = nextAuthorizationTickets.get(hashedId8str);			
				if (Aticket == null) {

					logAndPrint("Vehicle [" + itsId + "] Could not validate CAM, hashed AT [" + hashedId3str + "] is unknown",true);
				
					// Crazy mode comment next instruction
					//requestAuthorizationTicket = true;
				
					UnknownAuthorizationData unknownAD = new UnknownAuthorizationData(certId3, cam);	
					
					// put CAM in the pending verification list
					pendingCAMs.add(unknownAD); 
					
					//sync the access to this list (accessed in sending CAM and receiving CAM)
					 synchronized(unknwonATs){
						//put the certificateId in the list of unknown ATs to be requested in the next CAM
						unknwonATs.add(certId3); 
					    }	
					return;

				}

				if (v2x.verifyCertificate(Aticket, aaCert)) // TODO CHANGE TO ALL TRUSTED AA
				{
					if (v2x.processCAM(cam, Aticket)) {
						logAndPrint("Vehicle ["+itsId+"] Verified CAM [" + hashedId3str + "] with success!",false);
						
					} else {
						logAndPrint("Vehicle ["+itsId+"] Failed to verify CAM [" + hashedId3str + "]!",false);
					
					}
				} else {
					
					logAndPrint("Vehicle ["+itsId+"] Failed to verify AT ["+hashedId3str+"]!",false);
				}

				break;
			default:
				
				logAndPrint("Veicle ["+itsId+"] Cannot verify self-signed CAMs",false);
			}
		} catch (NoSuchAlgorithmException | IOException | BadContentTypeException e) {
			logAndPrint("Vehicle ["+itsId+"] Could not verify CAM reason"+e,true);
			e.printStackTrace();
		} 

	}

	public String getIstId() {
		return this.itsId;
	}

	public void refreshNearbyVehicles(List<Vehicle> nextVehicles) {
		nextVehicles.remove(this);
		this.nextVehicles = nextVehicles;
	}

	/**
	 * Help method that encodes bytes into string
	 * 
	 * @param bytes
	 * @return
	 */
	private String encodeHex(byte[] bytes) {
		return Hex.toHexString(bytes);
	}

	/**
	 * Help method that encodes bytes into string
	 * 
	 * @param bytes
	 * @return
	 */
	private byte[] decodeHex(String string) {
		return Hex.decode(string);
	}
	
	public boolean isSpecial() {
		return isSpecial;
	}

	public void setSpecial(boolean isSpecial) {
		this.isSpecial = isSpecial;
	}

	public String toString() {
		PublicKey pubKey = canonicalPair.getPublic();
		return "vehicle [\n" + "  id=" + itsId + "\n" + "  canonicalPubKey="
				+ Base64.getEncoder().encodeToString(pubKey.getEncoded()) + "\n" + "  canonicalAlgorithm="
				+ vehicleAlg.toString() + "\n" + "]";
	}
	
	public void logAndPrint(String msg, boolean newLineBefore) {
		if(this.isSpecial) {
			if(newLineBefore) {
				System.out.println();
			}
			System.out.println(msg);
		}
		logger.info(msg);
	}

}
