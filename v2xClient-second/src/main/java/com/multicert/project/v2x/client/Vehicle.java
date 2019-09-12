package com.multicert.project.v2x.client;

import java.io.IOException;
import java.io.OutputStream;
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
import java.util.concurrent.locks.ReentrantLock;

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
import sun.misc.BASE64Encoder;

public class Vehicle implements Runnable {

	private boolean isSpecial = false;
	private String itsId; // name of this vehicle
	private KeyPair canonicalPair = null; // keypair provided at manufacture to id this vehicle
	private EtsiTs103097Certificate trustAnchor; // the certificate of the root CA
	private Map<HashedId8, EtsiTs103097Certificate> trustStore = new HashMap<HashedId8, EtsiTs103097Certificate>(); // The
    static long startTime;																												// collection
																													// of
																													// trusted
																													// AA
																													// certificates
	private EtsiTs103097Certificate eaCert; // the certificate of the EA that will enroll this vehicle
	private EtsiTs103097Certificate aaCert; // the certificate of the AA that will authorize this vehicle

	private boolean configured = false;
	private boolean enrolled = false;

	private KeyPair enrollVerificationKey = null; // new verification keys to be certified by the enrollment credential
	private SecretKey secretKeyEA = null;// a secret key to encrypt the EcRequest and share with the EA

	private EtsiTs103097Certificate enrollmentCredential = null;

	private List<AuthorizationData> authorizationTicketPool = new ArrayList<AuthorizationData>(); // a pool of
																									// authentication
																									// tickets to use,
																									// where
																									// AuthorizationData
																									// = AT + sig
																									// keypair

	private static final int authorizationTicketNumber = 4; // number of ATs on this vehicle's pool
	private int authorizationTicketIndex = authorizationTicketNumber - 1; // the index of the current AT to be used to
																			// sign a message

	private static final int maxAuthorizationTicketUsage = 3; // maximum number of message signed by the same AT
	private int authorizationTicketCounter = maxAuthorizationTicketUsage; // current number of messages that will be
																			// signed by the same AT

	private List<Vehicle> nextVehicles = new ArrayList<Vehicle>();// A list of the nearby vehicles to send v2x messages
																	// to
	Map<String, EtsiTs103097Certificate> nextAuthorizationTickets = new HashMap<String, EtsiTs103097Certificate>(); // Store
																													// of
																													// the
																													// nearby
																													// vehicle's
																													// valid
																													// authorization
																													// tickets,
																													// where
																													// Key
																													// =
																													// hashedId8
																													// bytes
																													// and
																													// value
																													// =
																													// the
																													// certificate
	private boolean requestAuthorizationTicket = true;

	private List<UnknownAuthorizationData> unknownAuthorizationTickets = new ArrayList<UnknownAuthorizationData>(); // Pool
																													// of
																													// the
																													// current
																													// unknown
																													// authorization
																													// tickets,
																													// where
																													// UnknownAuthorizationData
																													// =
																													// AT
																													// +
																													// CAM

	AuthorizationData currentAuthorizationData = null;

	private List<EtsiTs103097Data> pendingCAMs = new ArrayList<EtsiTs103097Data>(); // A list of thr current CAMs which
																					// are pending verification (for
																					// example vehicle does not know the
																					// AT which signed it)

	private AlgorithmType vehicleAlg = null; // Public key algorithm that will be used for this vehicle's cryptographic
												// operations (signature and encryption)

	private V2X v2x = null; // instance of the interface with the v2x lib
	private RaControllerApi raApi = null; // instance to the RA API
	

	
	String newLine = System.getProperty("line.separator");// This will retrieve line separator independent on OS.

	ReentrantLock lock = new ReentrantLock();

	Timer CAMtimer = new Timer();
	TimerTask task = new TimerTask() {
		public void run() {
			try {
				calculateNextAuthorizationTicket();
				sendCAM();
			} catch (SignatureException | NoSuchAlgorithmException | IOException e) {
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

	public Vehicle(String itsId, KeyPair canonicalPair, AlgorithmType vehicleAlg) throws Exception {
		this.itsId = itsId;
		this.canonicalPair = canonicalPair;
		this.vehicleAlg = vehicleAlg;
		this.v2x = new V2XImpl();
		this.raApi = new RaControllerApi();
	}

	public void run() {
		try {
			//configureVehicle();
			//enrollVehicle();
			//authorizeVehicle();
			//CAMtimer.scheduleAtFixedRate(task, 0, 500);
			//includeCertTimer.scheduleAtFixedRate(includeCert, 0, 10000);
		} catch (Exception e) {

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
	public void configureVehicle()
			throws IllegalArgumentException, InvalidKeySpecException, IOException, NoSuchAlgorithmException {
		
		PublicVerificationKey PubVerKey = v2x.buildVerificationKey(canonicalPair.getPublic(), vehicleAlg);

		
		VehiclePojo vehicle = new VehiclePojo();
		vehicle.setPublicKey(encodeHex(PubVerKey.getEncoded())); // encoded public verification key to string
		vehicle.setVehicleId(itsId);
		ConfigResponse response = raApi.configureVehicleUsingPOST(vehicle);
		this.configured = response.getIsSuccess();

		if (configured) {


			trustAnchor = new EtsiTs103097Certificate(decodeHex(response.getTrustAnchor()));
			System.out.println("Object: Root CA , size: " + ObjectSizeFetcher.getObjectSize(encodeHex(trustAnchor.getEncoded())) + " bytes");

			eaCert = new EtsiTs103097Certificate(decodeHex(response.getEaCert()));
			System.out.println("Object: Enrollment CA , size: " + ObjectSizeFetcher.getObjectSize(encodeHex(eaCert.getEncoded())) + " bytes");

			aaCert = new EtsiTs103097Certificate(decodeHex(response.getAaCert()));
			System.out.println("Object: Authorization CA , size: " + ObjectSizeFetcher.getObjectSize(encodeHex(aaCert.getEncoded())) + " bytes");

			SequenceOfCertificate certSequence = new SequenceOfCertificate(decodeHex(response.getTrustedAA())); // get
																												// the
																												// sequence
																												// of
																												// trusted
																												// AA
																												// certificates
			EtsiTs103097Certificate[] certArray = certSequence.getCerts();
			trustStore = v2x.genTrustStore(certArray); // store the AA certificates in a trustStore
			System.out.println();
			System.out.println("[" + itsId + "] Vehicle configured with success!" + newLine + "EA name: "
					+ eaCert.getName() + " with certificate: " + v2x.genCertificateHashedId3(eaCert) + "" + newLine
					+ "AA name: " + aaCert.getName() + " with certificate: " + v2x.genCertificateHashedId3(aaCert));
			System.out.println();
		} else {
			System.out.println("[" + itsId + "] Could not configure vehicle");
		}

	}

	public void enrollVehicle() throws GeneralSecurityException, Exception {
		if (!configured) {
			System.out.println(
					"vehicle enrollment request denied, only enrolled vehicles can start the enrollment process");
			return;
		}
		System.out.println("[" + itsId + "] enrolling to: " + eaCert.getName());
		enrollVerificationKey = v2x.genKeyPair(vehicleAlg); // generate a new verification key pair
		System.out.println("Object: Enroll Key , size: " + ObjectSizeFetcher.getObjectSize(enrollVerificationKey.getPrivate().getEncoded()) + " bytes");
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
		System.out.println("[" + itsId + "] Vehicle enrolled with success!" + newLine + "" + enrollmentCredential);
		System.out.println();
		System.out.println();
		System.out.println("Object: EnrollmentCredential, size: " + ObjectSizeFetcher.getObjectSize(enrollmentCredential.getEncoded()) + " bytes");

	}

	public void authorizeVehicle() throws Exception {
		if (!enrolled) {
			System.out.println(
					"Authorization request denied, only enrolled vehicles can start an authorizationr request");
			return;
		}

		Boolean verification = true; // flag this AT request to be verified by the EA

		for (int x = 0; x < authorizationTicketNumber; x++) {

			KeyPair verificationKeys = v2x.genKeyPair(vehicleAlg); // generate a new verification key pair
			System.out.println("Object: AT Key , size: " + ObjectSizeFetcher.getObjectSize(verificationKeys.getPrivate().getEncoded()) + " bytes");

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
			
			//System.out.println(itsId+" AUTH: "+encodeHex(etsiRequest.getEncoded()));

			Response response = raApi.requestAuthorizationTicketUsingPOST(request, verification);

			if (!response.getIsSuccess()) // If the AA could not build an Authorization response for this request
			{
				System.out.println("Authorization request failed, " + response.getResponseMessage());
				continue;
			}

			InnerAtResponse innerResponse = v2x.processAtResponse(decodeHex(response.getRequestEncoded()), aaCert,
					secretKeyAA);

			if (innerResponse == null) {
				System.out.println("authorization response discarded"); // response could have been tampered, discard
																		// the response
				continue;
			}

			AuthorizationResponseCode responseCode = innerResponse.getResponseCode();
			if (responseCode != AuthorizationResponseCode.OK) // If the AA denied the enrollment of this vehicle
			{
				if (responseCode == AuthorizationResponseCode.ENROLLMENT_VERIFICATION_FAILED) // If the provided
																								// enrollment credential
																								// is not valid
				{
					System.out
							.println("Authorization request denied, response code: " + innerResponse.getResponseCode());
					break;
				}

				System.out.println("Authorization request denied, response code: " + innerResponse.getResponseCode());
				continue;
			}

			EtsiTs103097Certificate authTicket = innerResponse.getCertificate();
			System.out.println("Object: AuthorizationTicket, size: " + ObjectSizeFetcher.getObjectSize(authTicket.getEncoded()) + " bytes");

			AuthorizationData authData = new AuthorizationData(authTicket, verificationKeys);
			lock.lock();
			try {
				authorizationTicketPool.add(authData);
			} finally {
				lock.unlock();
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
				shuffleATs();

			}
		}
		authorizationTicketCounter--;
		currentAuthorizationData = authorizationTicketPool.get(authorizationTicketIndex);
	}

	public void sendCAM() throws SignatureException, NoSuchAlgorithmException, IOException {

            System.out.println();
            System.out.println();
            System.out.println("---------- Send CAM [" + itsId + "]  ----------");
            System.out.println();

            System.out.println("Vehicle [" + itsId + "]  Starting timer");
	        startTime = System.currentTimeMillis();

		String from = this.itsId; // just to print on receiver vehicle

		KeyPair signatureKeys = currentAuthorizationData.getSignatureKeys();
		EtsiTs103097Certificate authTicket = currentAuthorizationData.getAuthorizationTicket();

		String payload = "Dymmy payload";
			
	
		List<HashedId3> requestCertsAux = Collections.synchronizedList(new ArrayList());
		HashedId3[] requestCerts = null;
			synchronized (requestCertsAux) {
				 for (UnknownAuthorizationData unknowndata : unknownAuthorizationTickets) {
					requestCertsAux.add(unknowndata.getAuthorizationTicket());

						System.out.println("[" + itsId + "] Requesting AT: "+ unknowndata.getAuthorizationTicketAsString() +" to peer vehicles ");

				}
				 requestCerts = new HashedId3[requestCertsAux.size()];

				requestCerts = requestCertsAux.toArray(requestCerts);

					
			  }
			  EtsiTs103097Data cam = v2x.genCAM(authTicket, signatureKeys, vehicleAlg, payload, requestCerts, null,
						requestAuthorizationTicket);

            System.out.println("[" + itsId + "] Created CAM: " + v2x.genCertificateHashedId3(authTicket)
                    +  " includes full cert "+ requestAuthorizationTicket+"]");
            System.out.println();

				if(requestAuthorizationTicket){
					System.out.println("Object: full CAM, size: " + ObjectSizeFetcher.getObjectSize(encodeHex(cam.getEncoded())) + " bytes");

				}else{
					System.out.println("Object: hash CAM, size: " + ObjectSizeFetcher.getObjectSize(cam.getEncoded()) + " bytes");
				}

				requestAuthorizationTicket = false;

			for (Vehicle v : nextVehicles) {

					System.out.println("[" + itsId + "] Sending CAM signed with: " + v2x.genCertificateHashedId3(authTicket)
							+ " to Vehicle [" + v.getIstId() + "]");
					System.out.println();

				v.receiveCAM(cam, from);
			}

	}

	public void receiveCAM(EtsiTs103097Data cam, String from) {

            System.out.println();
            System.out.println();
        System.out.println("---------- Receive CAM [" + itsId + "]  ----------");
            System.out.println();

		try {
			// look for my cert on the requeste certs within the CAM
			List<HashedId3> requestedAts = v2x.getRequestedCerts(cam);
			for (HashedId3 hash : requestedAts) {
				EtsiTs103097Certificate authTicket = currentAuthorizationData.getAuthorizationTicket();
				HashedId3 authorizationTicketHash = v2x.genCertificateHashedId3(authTicket);
				if (java.util.Arrays.equals(authorizationTicketHash.getData(), hash.getData())) {

						System.out.println("Found somebody does not know my cert:" + authorizationTicketHash);

					requestAuthorizationTicket = true; // will send it next
				}
			}

			SignerIdentifier sigId = v2x.getSignerId(cam);
			switch (sigId.getType()) {
			case CERTIFICATE:
				
				SequenceOfCertificate certs = (SequenceOfCertificate) sigId.getValue();
				EtsiTs103097Certificate[] authTicket = certs.getCerts();
				EtsiTs103097Certificate ticket = authTicket[0];
				String hashId3String = encodeHex(v2x.genCertificateHashedId3(ticket).getData());
				String hashId8String = encodeHex(v2x.genCertificateHashedId8(ticket).getData());
				

					System.out.println("[" + itsId + "] Received CAM With full AT [" + hashId3String + "]");

				
				
				if (v2x.verifyCertificate(ticket, aaCert)) {
					lock.lock();

					nextAuthorizationTickets.put(hashId8String, ticket); // put the hashedId8 in the known ATs
					lock.unlock();

					// verify this CAM
					if (v2x.processCAM(cam, ticket)) {
                        System.out.println("CAM full cert verified with success!");


                        long endTime = System.currentTimeMillis();
                        System.out.println("Vehicle [" + itsId + "]  Stoping timer");
                        System.out.println("Total execution time: " + (endTime-startTime) + "ms");


					} else {

							System.out.println("Failed to verify the CAM's signature!");

					}

					// try to verify older CAMs signed untrusted ATs
					List<UnknownAuthorizationData> verifiedATs = new ArrayList<UnknownAuthorizationData>();

					lock.lock();

					for (UnknownAuthorizationData unknownAData : unknownAuthorizationTickets) {
						if (unknownAData.getAuthorizationTicketAsString().equals(hashId3String)) { // compare the unknwon AT with the trusted AT
							
							// Found one previously untrusted AT that is now trusted
							// verify its CAM
							if (v2x.processCAM(unknownAData.getCam(), ticket)) {
								verifiedATs.add(unknownAData);
                                System.out.println("CAM previously unknown verified with success!");
                                System.out.println("Vehicle [" + itsId + "]  Stoping timer");
                                long endTime = System.currentTimeMillis();
                                System.out.println("Final execution time: " + (endTime-startTime) + "ms");




							} else {

									System.out.println("Failed to verify the CAM's signature!");

							}
						}

					}
					unknownAuthorizationTickets.removeAll(verifiedATs); // remove the verified ATs from the list of
																		// unknwon ATs
					lock.unlock();

				} else {
					// TODO: PEDIR O CERTIFICADO DA CA!!!!!!!!!?

						System.out.println("Failed to verify the vehicle's authorization ticket!");


				}
				break;

			case DIGEST:
				HashedId8 certId8 = (HashedId8) sigId.getValue();
				HashedId3 certId3 = new HashedId3(certId8.getData());
				
				String hashedId8str = encodeHex(certId8.getData());
				String hashedId3str = encodeHex(certId3.getData());
				

					System.out.println("[" + itsId + "] Received CAM with hash AT [" + certId3 + "]");

				
				EtsiTs103097Certificate Aticket = nextAuthorizationTickets
						.get(hashedId8str);			
				if (Aticket == null) {
					

						System.out.println("[" + itsId + "] Could not validate CAM as the hash AT [" + hashedId3str + "] is unknown");
                    System.out.println("Vehicle [" + itsId + "]  marking mid timer");
                    long endTime = System.currentTimeMillis();
                    System.out.println("mid execution time: " + (endTime-startTime) + "ms");
					
					// get the hashedId3 of the missing certificate and store it in a list
					lock.lock();

					requestAuthorizationTicket = true;

					UnknownAuthorizationData unknownAt = new UnknownAuthorizationData(certId3, cam);
					unknownAuthorizationTickets.add(unknownAt); // put the unknown AT and CAM on the unknown list

					lock.unlock();

					return;

				}

				if (v2x.verifyCertificate(Aticket, aaCert)) // TODO CHANGE TO ALL TRUSTED AA
				{
					if (v2x.processCAM(cam, Aticket)) {

							System.out.println("CAM with hash verified with success!");
                        System.out.println("Vehicle [" + itsId + "]  Stoping timer");
                        long endTime = System.currentTimeMillis();
                        System.out.println("Total execution time: " + (endTime-startTime) + "ms");


					} else {

							System.out.println("Failed to verify the CAM's signature!");


					}
				} else {

						System.out.println("Failed to verify the vehicle's authorization ticket!");

				}

				break;
			default:

					System.out.println("Cannot verify self-signed CAMs");


			}
		} catch (NoSuchAlgorithmException | IOException | BadContentTypeException e) {

				System.out.println("Could not verify CAM");
			e.printStackTrace();
		} finally {
			if (lock.isLocked()) {
				lock.unlock();
			}
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

}
