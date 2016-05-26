package web.xml.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;




import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.objenesis.instantiator.basic.NewInstanceInstantiator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import web.xml.model.User;
import web.xml.model.Users;
import web.xml.service.UserService;

import com.sun.org.apache.xml.internal.serialize.XMLSerializer;


@Controller
@RequestMapping("/Index")
public class UserController {
	
	
	@Autowired
	UserService userSer;
	
	/**
	 * Ovo koliko se secam sada nista ne radi, to smo koristili za probu, ali mozda zatreba
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<User> getUser(@PathVariable(value="id") String username) throws IOException, JAXBException {
		
		Users usersi = (Users) userSer.findAll();
		
		
		for(User u: usersi.getKorisnik())
		{
			if(u.getUsername().equals(username))
			{
				return new ResponseEntity<User>(u, HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<User>(new User(), HttpStatus.NO_CONTENT);
	}
	
   /**
    * Ovde se odradjuje provera korisnika prilikom login-a.
    * @param postPayload
    * @return
    * @throws IOException
    * @throws JAXBException
    * @throws ServletException
    * @throws NoSuchAlgorithmException
    * @throws InvalidKeySpecException
    */
   @RequestMapping(value = "/checkUser", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<LoginResponse> checkUser(@RequestBody String postPayload) throws IOException, JAXBException, ServletException, NoSuchAlgorithmException, InvalidKeySpecException {
	   
	   //ovo je navodno zastita od xss napada, nesto sam tako nasao na netu.
	   String cleanPostPayload = Jsoup.clean(postPayload, Whitelist.basic());
	   
	   JSONObject json = null;
	   try
	   {
		   json = new JSONObject(cleanPostPayload);
	   }
	   catch(Exception e)
	   {
		   return new ResponseEntity<UserController.LoginResponse>(HttpStatus.NOT_ACCEPTABLE);
	   }
	   

	   String username = json.getString("username");
	   String password = json.getString("password");
	   
	   
        JAXBContext context = JAXBContext.newInstance(Users.class);
		
		// Unmarshaller je objekat zadužen za konverziju iz XML-a u objektni model
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		Users usersi = (Users) unmarshaller.unmarshal(new File("./data/xml/probaListaKorisnika.xml"));
		
		//NEMOJTE DA BRISETE OVO, ZEZA ME ADRESA I NECE DA MI UCITA
	//	Users usersi = (Users) unmarshaller.unmarshal(new File("D:/4. godina/XML/app/HelloWeb01/HelloWeb/data/xml/probaKorisnik.xml"));
		
		
		for(User u: usersi.getKorisnik())
		{
			if(u.getUsername().equals(username))
			{
				if(userSer.autenticate(password, u.getPassword(), u.getSalt()))
				{
					
					String token = Jwts.builder().setSubject(u.getUsername()).claim("roles", u.getVrsta()).setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "asdf").compact();
					return new ResponseEntity<LoginResponse>(new LoginResponse(token), HttpStatus.OK); 
				}
			}
		}
		
		return new ResponseEntity<LoginResponse>(HttpStatus.NO_CONTENT);	      	  
   }
   
   /**
    * Registracija korisnika
    * @param postPayload
    * @return
    * @throws IOException
    * @throws JAXBException
    * @throws NoSuchAlgorithmException
    * @throws InvalidKeySpecException
    * @throws JSONException
    */
   @RequestMapping(value = "/registration", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody User registerUser(@RequestBody String postPayload) throws IOException, JAXBException, NoSuchAlgorithmException, InvalidKeySpecException, JSONException 
   {
	   userSer.preSave(postPayload, new File("./data/xml/probaListaKorisnika.xml"));
	   
	   userSer.save(new File("./data/xml/probaListaKorisnika.xml"));
	   
	   
	   return new User();
   }
   
   /**
    * Ova unutrasnja klasa samo pomaze da se kreira token prilikom registracije
    * @author Marko
    *
    */
   @SuppressWarnings("unused")
   private static class LoginResponse {
       public String token;

       public LoginResponse(final String token) {
           this.token = token;
       }
   }
   
}
