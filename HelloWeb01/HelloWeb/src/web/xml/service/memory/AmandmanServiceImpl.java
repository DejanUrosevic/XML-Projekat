package web.xml.service.memory;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jaxb.from.xsd.Amandman;
import jaxb.from.xsd.Propis;
import web.xml.model.Propisi;
import web.xml.model.User;
import web.xml.service.AmandmanService;
import web.xml.service.PropisService;

@Service
public class AmandmanServiceImpl implements AmandmanService{

	@Autowired
	PropisService propisSer;
	
	public static int getID() {
		Random broj = new Random();
		return broj.nextInt(1000000);
	}
	
	@Override
	public Amandman findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Amandman findAll() throws JAXBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(File f) throws FileNotFoundException, JAXBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Long id) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void marshall(Amandman t, File f) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Amandman.class);

		Marshaller marshaller = context.createMarshaller();

		// Pode�avanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		// Umesto System.out-a, mo�e se koristiti FileOutputStream
		marshaller.marshal(t, f);
		
	}

	@Override
	public Amandman unmarshall(File f) throws JAXBException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void dodajAmandman(String postPayLoad, User korisnik) throws DatatypeConfigurationException, JAXBException {
		JSONObject json = new JSONObject(postPayLoad);
		Amandman a = new Amandman();
		a.setResenje(json.getString("clanTekst"));
		a.setObrazlozenje(json.getString("amandmanObrazlozenje"));
		
		GregorianCalendar c = new GregorianCalendar(); 
		c.setTime(new Date()); 
		XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		
		a.setDatum(date2);
		Integer id = getID();
		a.setID(BigInteger.valueOf(Long.parseLong(id.toString())));
		a.setPodnosilac(korisnik.getIme() + " " + korisnik.getPrezime());
		a.setMesto("Novi Sad");
		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
		int propisid = json.getInt("propisId");
		Propis propis = null;
//		for (Propis p: propisi.getPropisi()) {
//			if(p.getID().equals(BigInteger.valueOf(propisid))){
////				a.setPropis(p);
//				propis = p;
//				p.getAmandman().add(a);
//			
//				break;
//			}
//		}
		for (int i = 0; i < propisi.getPropisi().size(); i++) {
			if(propisi.getPropisi().get(i).getID().equals(BigInteger.valueOf(propisid))){
				propisi.getPropisi().get(i).getAmandman().add(a);
				break;
			}
		}
		propisSer.marshall(propisi, new File("./data/xml/propisi.xml"));
		marshall(a, new File("./data/xml/amandman.xml"));
		
	}

}
