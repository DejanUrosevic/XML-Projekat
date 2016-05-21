package web.xml.service.memory;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.stereotype.Service;
import web.xml.model.User;
import web.xml.model.Users;
import web.xml.service.UserService;

@Service
public class UserServiceImpl implements UserService
{

	@Override
	public User findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override 
	public User save(User t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(Long id) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Users unmarshall(File f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void marshall(File f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// TODO Auto-generated method stub
		
		String algorithm = "PBKDF2WithHmacSHA1";
		  // SHA1 hash je duzine 160 bita
		int derivedKeyLength = 160;
		//broj iteracija
		int iterations = 1000;
			
		 
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
		  // vratimo hashiranu vrednost lozinke
		return f.generateSecret(spec).getEncoded();
	}

	@Override
	public boolean autenticate(String loginPassword, byte[] databasePassword, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// TODO Auto-generated method stub
		
		byte[] hashedAttemptedPassword = hashPassword(loginPassword, salt); 
		  //hash-evi se porede
		return Arrays.equals(hashedAttemptedPassword, databasePassword);
		
	}

	@Override
	public byte[] generateSalt() throws NoSuchAlgorithmException {
		 SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		 byte[] salt = new byte[8];
		 random.nextBytes(salt);
		 return salt;
	}

}
