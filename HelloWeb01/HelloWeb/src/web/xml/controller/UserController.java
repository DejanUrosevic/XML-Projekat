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
import org.json.JSONObject;
import org.json.XML;
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

import com.sun.org.apache.xml.internal.serialize.XMLSerializer;


@Controller
@RequestMapping("/Index")
public class UserController {
	
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<User> getUser(@PathVariable(value="id") long id) throws IOException, JAXBException {
		JAXBContext context = JAXBContext.newInstance(Users.class);
		
		// Unmarshaller je objekat zadužen za konverziju iz XML-a u objektni model
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		 
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		Users usersi = (Users) unmarshaller.unmarshal(new File("./data/xml/probaKorisnik.xml"));
		
		//NEMOJTE DA BRISETE OVO, ZEZA ME ADRESA I NECE DA MI UCITA
		//Users usersi = (Users) unmarshaller.unmarshal(new File("D:/4. godina/XML/app/HelloWeb01/HelloWeb/data/xml/probaKorisnik.xml"));
		
		for(User u: usersi.getKorisnik())
		{
			if(u.getID() == id)
			{
				return new ResponseEntity<User>(u, HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<User>(new User(), HttpStatus.NO_CONTENT);
	}
	
   
   @RequestMapping(value = "/checkUser", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody LoginResponse checkUser(@RequestBody String postPayload) throws IOException, JAXBException, ServletException {
	   
	   JSONObject json = new JSONObject(postPayload);

	   String username = json.getString("username");
	   String password = json.getString("password");
	   
	   
	   
	   /*
	   Users users = new Users();
	   List<User> sviUseri = new ArrayList<User>();   
	   
       
       User us1 = new User("Pera", "Peric", "p", "p", "odbornik", 3123);
       User us2 = new User("Dragan", "Maric", "p", "p", "gradjanin", 123);
       User us3 = new User("Das", "Asd", "p", "p", "predsednik skupstine", 63);

       sviUseri.add(us1);
       sviUseri.add(us2);
       sviUseri.add(us3);
       
       users.setKorisnik(sviUseri);
	  */
        JAXBContext context = JAXBContext.newInstance(Users.class);
		
		// Unmarshaller je objekat zadužen za konverziju iz XML-a u objektni model
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		Users usersi = (Users) unmarshaller.unmarshal(new File("./data/xml/probaKorisnik.xml"));
		
		//NEMOJTE DA BRISETE OVO, ZEZA ME ADRESA I NECE DA MI UCITA
		//Users usersi = (Users) unmarshaller.unmarshal(new File("D:/4. godina/XML/app/HelloWeb01/HelloWeb/data/xml/probaKorisnik.xml"));
		
		
		for(User u: usersi.getKorisnik())
		{
			if(u.getUsername().equals(username))
			{
				if(u.getPassword().equals(password))
				{
					String token = Jwts.builder().setSubject(u.getUsername()).claim("roles", u.getVrsta()).setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "asdf").compact();
					return new LoginResponse(token); 
				}
			}
		}
		return null;
	
	   
		
       	  
   }
   
   @RequestMapping(value = "/registration", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody User registerUser(@RequestBody String postPayload) throws IOException, JAXBException 
   {
	   JSONObject json = new JSONObject(postPayload);
	   User user = new User(json.getString("ime"), json.getString("prezime"), 
			   json.getString("username"), json.getString("password"), "gradjanin", 39);
	   
	   JAXBContext context = JAXBContext.newInstance(Users.class);
		
		// Unmarshaller je objekat zadužen za konverziju iz XML-a u objektni model
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		
		// Unmarshalling generiše objektni model na osnovu XML fajla
		Users usersi = (Users) unmarshaller.unmarshal(new File("./data/xml/probaKorisnik.xml"));

		
		//NEMOJTE DA BRISETE OVO, ZEZA ME ADRESA I NECE DA MI UCITA
		//Users usersi = (Users) unmarshaller.unmarshal(new File("D:/4. godina/XML/app/HelloWeb01/HelloWeb/data/xml/probaKorisnik.xml"));
		
		
		usersi.getKorisnik().add(user);
		
		Marshaller marshaller = context.createMarshaller();
		
		// Podešavanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Umesto System.out-a, može se koristiti FileOutputStream
		marshaller.marshal(usersi, new File("data\\xml\\probaKorisnik.xml"));

		//NEMOJTE DA BRISETE OVO, ZEZA ME ADRESA I NECE DA MI UCITA
		//marshaller.marshal(usersi, new File("D:/4. godina/XML/app/HelloWeb01/HelloWeb/data/xml/probaKorisnik.xml"));
	   
	   
	   return new User();
   }
   
   @SuppressWarnings("unused")
   private static class LoginResponse {
       public String token;

       public LoginResponse(final String token) {
           this.token = token;
       }
   }
   
}
