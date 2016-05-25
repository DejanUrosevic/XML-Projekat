package web.xml.service;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import web.xml.model.User;
import web.xml.model.Users;

public interface UserService extends CrudService<User>
{
	
	/**
	 * Generisanje salt-a za pomoc u heshiranju lozinke
	 * @return generisani salt
	 * @throws NoSuchAlgorithmException
	 */
	public byte[] generateSalt() throws NoSuchAlgorithmException;
	
	/**
	 * Heshira lozinku
	 * @param password
	 * @param salt
	 * @return vraca heshirani oblik loznike
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException;
	
	/**
	 * Provera da li je lozinka koja je dobijena sa klijenta ista kao i na bazi
	 * @param loginPassword
	 * @param databasePassword
	 * @param salt
	 * @return true ako jeste, false ako nije
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public boolean autenticate(String loginPassword, byte[] databasePassword, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
