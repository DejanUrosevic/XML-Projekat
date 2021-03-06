package web.xml.service.memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;

/*
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.DatabaseClientFactory.Authentication;


import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;*/

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import web.xml.crl.Sertifikati;
import web.xml.model.User;
import web.xml.model.Users;
import web.xml.role.Role;
import web.xml.role.Role.Rola;
import web.xml.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Override
	public Users findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Users findAll() throws JAXBException {
		/*DatabaseClient client = DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37", "tim37", "tim37",
				Authentication.valueOf("DIGEST"));*/
		
		DatabaseClient client = DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37", "tim37", "tim37",
				Authentication.valueOf("DIGEST"));
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();

		// A handle to receive the document's content.
		DOMHandle content = new DOMHandle();

		DocumentMetadataHandle metadata = new DocumentMetadataHandle();

		// A document URI identifier.
		String docId = "/korsnici.xml";

		xmlManager.read(docId, metadata, content);

		// Retrieving a document node form DOM handle.
		Document doc = content.get();

		JAXBContext context = JAXBContext.newInstance(Users.class);

		// Unmarshaller je objekat zaduï¿½en za konverziju iz XML-a u objektni
		// model
		Unmarshaller unmarshaller = context.createUnmarshaller();

		return (Users) unmarshaller.unmarshal(doc);
	}

	@Override
	public void save(File f) throws FileNotFoundException {
		DatabaseClient client = DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37", "tim37", "tim37",
				Authentication.valueOf("DIGEST"));
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();

		String docId = "/korsnici.xml";
		String collId = "/skupstina/korisnici";

		InputStreamHandle handle = new InputStreamHandle(new FileInputStream(f.getAbsolutePath()));
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		metadata.getCollections().add(collId);

		xmlManager.write(docId, metadata, handle);

		client.release();
	}

	@Override
	public void remove(Long id) throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// TODO Auto-generated method stub

		String algorithm = "PBKDF2WithHmacSHA1";
		// SHA1 hash je duzine 160 bita
		int derivedKeyLength = 160;
		// broj iteracija
		int iterations = 1000;

		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
		// vratimo hashiranu vrednost lozinke
		return f.generateSecret(spec).getEncoded();
	}

	@Override
	public boolean autenticate(String loginPassword, byte[] databasePassword, byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// TODO Auto-generated method stub

		byte[] hashedAttemptedPassword = hashPassword(loginPassword, salt);
		// hash-evi se porede
		return Arrays.equals(hashedAttemptedPassword, databasePassword);
	}

	@Override
	public byte[] generateSalt() throws NoSuchAlgorithmException {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		return salt;
	}

	@Override
	public Users unmarshall(File f) throws JAXBException {
		// TODO Auto-generated method stub

		JAXBContext context = JAXBContext.newInstance(Users.class);

		// Unmarshaller je objekat zaduï¿½en za konverziju iz XML-a u objektni
		// model
		Unmarshaller unmarshaller = context.createUnmarshaller();

		// Unmarshalling generiï¿½e objektni model na osnovu XML fajla
		return (Users) unmarshaller.unmarshal(f);
	}

	@Override
	public void marshall(Users t, File f) throws JAXBException {
		// TODO Auto-generated method stub

		JAXBContext context = JAXBContext.newInstance(Users.class);

		Marshaller marshaller = context.createMarshaller();

		// Podeï¿½avanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		// Umesto System.out-a, moï¿½e se koristiti FileOutputStream
		marshaller.marshal(t, f);
	}

	@Override
	public File preSave(String podaci, File f) throws NoSuchAlgorithmException, InvalidKeySpecException, JSONException,
			JAXBException, FileNotFoundException {
		
		Random rand = new Random();
		
		JSONObject json = new JSONObject(podaci);
		User user = new User(json.getString("ime"), json.getString("prezime"), json.getString("username"), "odbornik",
				Long.parseLong(String.valueOf(rand.nextInt(50000))), json.getString("jksPutanja"), json.getString("alias"));

		// ovde radimo hash password-a i takodje pamtimo u bazu i salt kojim je
		// password odradjen
		byte[] salt = generateSalt();
		user.setSalt(salt);
		user.setPassword(hashPassword(json.getString("password"), salt));

		Users usersi = unmarshall(f);

		usersi.getKorisnik().add(user);

		marshall(usersi, f);

		return null;
	}

	@Override
	public String getCertificateSerialNumber(Certificate cert) {
		// TODO Auto-generated method stub
		X509Certificate cer = (X509Certificate) cert;
 
		return cer.getSerialNumber().toString(16);
	}

	@Override
	public boolean isValidCertificate(String serialNumber) throws JAXBException {
		// TODO Auto-generated method stub
		JAXBContext context = JAXBContext.newInstance(Sertifikati.class);

		// Unmarshaller je objekat zaduï¿½en za konverziju iz XML-a u objektni
		// model
		Unmarshaller unmarshaller = context.createUnmarshaller();

		// Unmarshalling generiï¿½e objektni model na osnovu XML fajla
		Sertifikati sertifikati = (Sertifikati) unmarshaller.unmarshal(new File("data\\sertifikati\\crl.xml"));
		
		for(int i = 0; i < sertifikati.getSerijskiBroj().size(); i++){
			if(sertifikati.getSerijskiBroj().get(i).equals(serialNumber)){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public User getUserFromJWT(HttpServletRequest req) throws ServletException, JAXBException {
		// TODO Auto-generated method stub
		
		final HttpServletRequest request = (HttpServletRequest) req;
		final String authHeader = request.getHeader("Authorization");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.equals("Bearer null") || authHeader.endsWith("null")) {
			throw new ServletException("Missing or invalid Authorization header.");
		}
		
		String token = authHeader.substring(7); // The part after "Bearer "

		Claims claims = Jwts.parser().setSigningKey("asdf").parseClaimsJws(token).getBody();

		Users korisnici = unmarshall(new File("data\\xml\\probaListaKorisnika.xml"));
		
		for(User kor : korisnici.getKorisnik()){
			if(kor.getUsername().equals(claims.getSubject())){
				return kor;
			}
		}
		
		return null;
	}
	
	@Override
	public Rola getRolaPermisije(User user) throws JAXBException {
		// TODO Auto-generated method stub
		Role role = getRole();
		Rola rola = null;
		
		for(Rola r : role.getRola()){
			if(r.getNaziv().equals(user.getVrsta())){
				rola = r;
			}
		}
		
		return rola;
	}

	@Override
	public Role getRole() throws JAXBException {
		// TODO Auto-generated method stub
		DatabaseClient client = DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37", "tim37", "tim37",
				Authentication.valueOf("DIGEST"));
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();

		// A handle to receive the document's content.
		DOMHandle content = new DOMHandle();

		DocumentMetadataHandle metadata = new DocumentMetadataHandle();

		// A document URI identifier.
		String docId = "/role.xml";

		xmlManager.read(docId, metadata, content);

		// Retrieving a document node form DOM handle.
		Document doc = content.get();

		JAXBContext context = JAXBContext.newInstance(Role.class);

		// Unmarshaller je objekat zadu�en za konverziju iz XML-a u objektni
		// model
		Unmarshaller unmarshaller = context.createUnmarshaller();

		return (Role) unmarshaller.unmarshal(doc);
	}

}
