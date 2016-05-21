package web.xml.model;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "korisnik")
public class User 
{

	private String ime;
	private String prezime;
	private String username;
	private byte[] password;
	private String vrsta;
	private long id;
	private byte[] salt; 
	
	
	public User()
	{
		
	}

	public User(String ime, String prezime, String username,
			String vrsta, long id) {
		super();
		this.ime = ime;
		this.prezime = prezime;
		this.username = username;
		this.vrsta = vrsta;
		this.id = id;
	}

	@XmlElement(required = true)
	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	@XmlElement(required = true)
	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	@XmlElement(required = true)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@XmlElement(required = true)
	public byte[] getPassword() {
		return password;
	}

	public void setPassword(byte[] password) {
		this.password = password;
	}

	@XmlElement(required = true)
	public String getVrsta() {
		return vrsta;
	}

	public void setVrsta(String vrsta) {
		this.vrsta = vrsta;
	}

	@XmlElement(required = true)
	public long getID() {
		return id;
	}

	public void setID(long id) {
		this.id = id;
	}

	@XmlElement(required = true)
	public byte[] getSalt() {
		return salt;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
	
	/*
	
	public Users getAllUsers() throws JAXBException
	{
		JAXBContext context = JAXBContext.newInstance(User.class);
		
		// Unmarshaller je objekat zadužen za konverziju iz XML-a u objektni model
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		Users users = (Users) unmarshaller.unmarshal(new File("./data/xml/korisnik1.xml"));
		
		return users;
	}
	
	*/	
}
