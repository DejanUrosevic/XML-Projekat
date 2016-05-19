package web.xml.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlRootElement(name="korisnici")
public class Users 
{

	private List<User> korisnik = new ArrayList<User>();
	
	public Users()
	{
		 
	}

	@XmlElement(name="korisnik")
	public List<User> getKorisnik() {
		return korisnik;
	}

	public void setKorisnik(List<User> korisnik) {
		this.korisnik = korisnik;
	}

	

	
	
}
