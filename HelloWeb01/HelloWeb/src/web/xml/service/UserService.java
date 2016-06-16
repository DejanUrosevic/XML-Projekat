package web.xml.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import org.json.JSONException;

import web.xml.model.User;
import web.xml.model.Users;
import web.xml.role.Role;
import web.xml.role.Role.Rola;

public interface UserService extends CrudService<Users> {

	/**
	 * Podesavanja radi lakseg save-a na bazu, ovde se takodje radi hash&salt
	 * metoda za lozinku
	 * 
	 * @param podaci
	 *            koji su dobijeni sa fronta
	 * @param f
	 *            xml fajl koji se koristi
	 * @return f fajl koji predstavlja xml koji cemo zapamtati na bazi
	 * @throws NoSuchAlgorithmException
	 * @throws JSONException
	 * @throws InvalidKeySpecException
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public File preSave(String podaci, File f) throws NoSuchAlgorithmException, InvalidKeySpecException, JSONException,
			JAXBException, FileNotFoundException;

	/**
	 * Generisanje salt-a za pomoc u heshiranju lozinke
	 * 
	 * @return generisani salt
	 * @throws NoSuchAlgorithmException
	 */
	public byte[] generateSalt() throws NoSuchAlgorithmException;

	/**
	 * Heshira lozinku
	 * 
	 * @param password
	 * @param salt
	 * @return vraca heshirani oblik loznike
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException;

	/**
	 * Provera da li je lozinka koja je dobijena sa klijenta ista kao i na bazi
	 * 
	 * @param loginPassword
	 * @param databasePassword
	 * @param salt
	 * @return true ako jeste, false ako nije
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public boolean autenticate(String loginPassword, byte[] databasePassword, byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException;
	
	public String getCertificateSerialNumber(Certificate cert);
	
	public boolean isValidCertificate(String serialNumber) throws JAXBException;
	
	public User getUserFromJWT(HttpServletRequest req) throws ServletException, JAXBException;
	
	public Rola getRolaPermisije(User user) throws JAXBException;
	
	public Role getRole() throws JAXBException;
}
