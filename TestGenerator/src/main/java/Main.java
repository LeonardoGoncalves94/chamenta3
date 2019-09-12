import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Main {

	static final private int MAX_VEHICLES = 200;
	static List<String> vehicleids = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception {
		RandomStringGenerator gen = new RandomStringGenerator(9); 
		for(int i = 0; i < MAX_VEHICLES; i ++) 
		{
			String itsId = gen.nextString();
			vehicleids.add(itsId);

		}
		
		genConfigFile();
		genConEnrollFile();
		genAuthFile();
		
	}
	
	public static void genConfigFile() throws IOException {
		FileWriter csvWriter = new FileWriter("vehicleid.csv");
		RandomStringGenerator gen = new RandomStringGenerator(9); 

		for(int i = 0; i < MAX_VEHICLES; i ++) 
		{
			String itsId = gen.nextString();
	
			csvWriter.append("String");
			csvWriter.append(",");
			csvWriter.append("8084c7da014494a2cad1956945cc03f615f8df9cccd4f19e93a1036da7df14ec89b3e264915cd5338110142870a4f14705d099c578ca819642acb5265a6f5baf9d6d");
			csvWriter.append(",");
			csvWriter.append(vehicleids.get(i));
			csvWriter.append(",");
			csvWriter.append("0");
			csvWriter.append('\n');
		}
		csvWriter.flush();
		csvWriter.close();
	}
	
	public static void genConEnrollFile() throws IOException {
		FileWriter csvWriter = new FileWriter("enrollreq.csv");
		RandomStringGenerator gen = new RandomStringGenerator(9); 

		for(int i = 0; i < MAX_VEHICLES; i ++) 
		{
			String itsId = gen.nextString();
	
			csvWriter.append("EnrollmentCa");
			csvWriter.append(",");
			csvWriter.append("03820101822634a5eef3d1dc55808273f895e7df809fd6a384fe89a9ce3eb5f1679dfe9a866a45424872e3368c4894f215a2e92138d35d0589958109c8e9f5b9497f1320358f3c92bd144b31aaafa68019d5ebc92679738780889230820110a8c24fb2018d21328a81c2718ff9aefe873dea00d018f30ccbfeec096640203a3885dfb5dca91e2900182c532891dffcb11f5a5f1d339f34abe6fae9d9822057572aa4a1d256e5bd5cf59b490a02001991e1291d44008e2bbe973d45ecdee72bce65d8bdc3c865b4aaf5f4ad7d2388752316c7ddab074711a43acaddb7fc7500a35583e9ae3025c71b6c9b188df05a2e54d41241f7649713f56786a369948b5b5896488d9123fd950c59226d91bd91b5d33ca16365c5f3082ae277e1bde2dd2455ff3c418b6391f9d442687f7821606b5b2fa518952ec6631cdb279fcdeaf574329622b9f8d647806d8deee5de7ae2651e7ce95702587e565c0b54615918ccae94148e4ff07c28a2abd10bbdaefade3d");
			csvWriter.append(",");
			csvWriter.append(vehicleids.get(i));
			csvWriter.append(",");
			csvWriter.append("0");
			csvWriter.append('\n');
		}
		csvWriter.flush();
		csvWriter.close();
	}
	
	public static void genAuthFile() throws IOException {
		FileWriter csvWriter = new FileWriter("authreq.csv");
		RandomStringGenerator gen = new RandomStringGenerator(9); 

		for(int i = 0; i < MAX_VEHICLES; i ++) 
		{
			String itsId = gen.nextString();
	
			csvWriter.append("AuthorizationCa");
			csvWriter.append(",");
			csvWriter.append("03820101826402341848c9865680825887a8d0c3993291dd21b618699b879bde0899f3eecb80f2f2c3da96d8aee4ee835171901a55afcece0e0e55b8453b971bed6c33f25b2b9e54f2b3b79d0f9d258017455ade23858a3d6167335982013dd6dc4c2b06882bf036f82a3da0fb0d1e4950e6269053ae3fb406b15fb164b5cf3bc5b2c4edc68177f15fbbcac1385f296cefffdfcfb6df8565670e6eca2bbb6c21f53a53227885d413ae1f7154cfb28a0c6d8422ca2ddac41adbb104ea12a7cf0181c408386c9c27a2f6508c01393e834db5e11afa126805ad4364e3c5d930168112b37c97b9c8dec3aff86071c2b35b85a384da3776bf345c526dde63323bed0bb8296f061f916a415999f7720b75b84a2690e18f7867afbd071580f805a2f39572f97a61ec2bb3048a2e1ec289f2c42fa816be417cad7c853348dd3682c92a57ceb6802bce9261e21358b1892557c3961fafde36e44dc72317ed7a20e2c055097710cafbfd1bd58a2dfedb371365eb6328af9e2d7dbc1274e4b038e6d48d12dddc2f5ab0544d8115c447dbe221c5f80912bedc4c814a9ffbffcc73c8");
			csvWriter.append(",");
			csvWriter.append(vehicleids.get(i));
			csvWriter.append(",");
			csvWriter.append("0");
			csvWriter.append('\n');
		}
		csvWriter.flush();
		csvWriter.close();
	}
}
