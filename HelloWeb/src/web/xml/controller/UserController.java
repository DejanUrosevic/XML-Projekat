package web.xml.controller;

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
import java.util.List;

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
	
	@RequestMapping(value = "/checkUser", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<User> getUser() throws IOException, JAXBException {
		User us1 = new User("Pera", "Peric", "p", "p", "odbornik", 3123);
		return new ResponseEntity<User>(us1, HttpStatus.OK);
	}
	
   
   @RequestMapping(value = "/checkUser", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody User checkUser(@RequestBody String postPayload) throws IOException, JAXBException {
	   
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
		
		User us3 = new User("Das", "Asd", "p", "p", "predsednik skupstine", 63);
		
		for(User u: usersi.getKorisnik())
		{
			if(u.getUsername().equals(username))
			{
				if(u.getPassword().equals(password))
				{
					return u;
				}
			}
		}
		return new User();
	
	   
		
       	  
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
		
		
		usersi.getKorisnik().add(user);
		
		Marshaller marshaller = context.createMarshaller();
		
		// Podešavanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Umesto System.out-a, može se koristiti FileOutputStream
		marshaller.marshal(usersi, new File("data\\xml\\probaKorisnik.xml"));
	   
	   
	   return new User();
   }
   
   public static void main(String[] args) throws JAXBException
   {
	   User us1 = new User("Pera", "Peric", "p", "p", "odbornik", 3123);
	   User us2 = new User("Dragan", "Maric", "p", "p", "gradjanin", 123);
       User us3 = new User("Das", "Asd", "p", "p", "predsednik skupstine", 63);
       
       List<User> lista = new ArrayList<User>();
       lista.add(us1);
       lista.add(us2);
       lista.add(us3);
       
       Users users33 = new Users();
       users33.setKorisnik(lista);

       /*
       
       JAXBContext jc = JAXBContext.newInstance(Users.class);
       Marshaller mrs = jc.createMarshaller();
       mrs.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
       mrs.marshal(users33, System.out);
       mrs.marshal(users33, new File("data\\xml\\probaListaKorisnika.xml"));
	       
	       //-------------
	   /*
	       JAXBContext jc = JAXBContext.newInstance(User.class);
	       Marshaller mrs = jc.createMarshaller();
	       mrs.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	       mrs.marshal(us1, System.out);
	       mrs.marshal(us1, new File("data\\xml\\probaKorisnik.xml"));
	   */
	   /*
	   JAXBContext context = JAXBContext.newInstance(User.class);
		
		// Unmarshaller je objekat zadužen za konverziju iz XML-a u objektni model
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		User user = (User) unmarshaller.unmarshal(new File("./data/xml/probaKorisnik.xml"));
		
		System.out.println(user.getIme() + " " + user.getPrezime());
		*/
   }
   
}
