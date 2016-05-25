package web.xml.service.memory;

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import jaxb.from.xsd.Clan;
import jaxb.from.xsd.Propis;
import jaxb.from.xsd.Clan.Sadrzaj;
import jaxb.from.xsd.Clan.Sadrzaj.Stav;
import jaxb.from.xsd.Propis.Deo;
import jaxb.from.xsd.Propis.Deo.Glava;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import web.xml.model.Propisi;
import web.xml.model.Users;
import web.xml.service.PropisService;

@Service
public class PropisServiceImpl implements PropisService
{
	
	public static int getID(){
		Random broj = new Random();
		return broj.nextInt(100000);
	}
	
	@Override
	public Propisi findOne(Long id) { 
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Propisi> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Propisi save(Propisi t) { 
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(Long id) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Propis dodajPropis(String requestData) {
		// TODO Auto-generated method stub
		
		JSONObject json = new JSONObject(requestData);

		String tekstClana = "";
		String redniBroj = "";
		String tekstStava = "";
		String nazivGlave = "";
		
		String propisNaziv = json.getString("nazivPropisa");
		String nazivDela = json.getString("nazivDeo");
		
		String nazivClana = json.getString("nazivClana");
		String opisClana = json.getString("opisClana");
		try
		{
			tekstClana = json.getString("tekstClana");
			
		}
		catch(org.json.JSONException e)
		{
			//e.printStackTrace();
		}
		try
		{
			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");
		}
		catch(org.json.JSONException e)
		{
			//e.printStackTrace();
		}
		try
		{
			nazivGlave = json.getString("nazivGlave");
		}
		catch(org.json.JSONException e)
		{
			
		}
		
		Propis propis = new Propis();
		Deo deo = new Deo();
		Glava glava = new Glava();
		Clan clan = new Clan();
		Sadrzaj sadrzaj = new Sadrzaj();
		Stav stav = new Stav();
		
		if(!redniBroj.equals(""))
		{
			stav.setRedniBroj(Long.parseLong(redniBroj));
			stav.setTekst(tekstStava);
			sadrzaj.getStav().add(stav);
		}
		if(!tekstClana.equals(""))
		{
			sadrzaj.getTekst().add(tekstClana);
		}
		if(!nazivGlave.equals(""))
		{
			glava.getClan().add(clan);
			glava.setID(BigInteger.valueOf(getID()));
			glava.setNaziv(nazivGlave);
			deo.getGlava().add(glava);
		}
		
		
		clan.setID(BigInteger.valueOf(getID()));
		clan.setNaziv(nazivClana);
		clan.setOpis(opisClana);
		clan.setSadrzaj(sadrzaj);
		
		if(nazivGlave.equals(""))
		{
			deo.getClan().add(clan);
		}
		
		deo.setID(BigInteger.valueOf(getID()));
		deo.setNaziv(nazivDela);
		
		
		propis.setID(BigInteger.valueOf(getID()));
		propis.setNaziv(propisNaziv);
		propis.getDeo().add(deo);
		
		return propis;
	}

	@Override
	public Propisi dodajDeo(String requestData) throws JAXBException {
		// TODO Auto-generated method stub
		
		JSONObject json = new JSONObject(requestData);

		String tekstClana = "";
		String redniBroj = "";
		String tekstStava = "";
		String nazivGlave = "";
		
		String nazivDela = json.getString("nazivDeo");
		
		String nazivClana = json.getString("nazivClana");
		String opisClana = json.getString("opisClana");
		try
		{
			tekstClana = json.getString("tekstClana");
			
		}
		catch(org.json.JSONException e)
		{
			//e.printStackTrace();
		}
		try
		{
			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");
		}
		catch(org.json.JSONException e)
		{
			//e.printStackTrace();
		}
		try
		{
			nazivGlave = json.getString("nazivGlave");
		}
		catch(org.json.JSONException e)
		{
			
		}
		
		Deo deo = new Deo();
		Glava glava = new Glava();
		Clan clan = new Clan();
		Sadrzaj sadrzaj = new Sadrzaj();
		Stav stav = new Stav();
		
		if(!redniBroj.equals(""))
		{
			stav.setRedniBroj(Long.parseLong(redniBroj));
			stav.setTekst(tekstStava);
			sadrzaj.getStav().add(stav);
		}
		if(!tekstClana.equals(""))
		{
			sadrzaj.getTekst().add(tekstClana);
		}
		if(!nazivGlave.equals(""))
		{
			glava.getClan().add(clan);
			glava.setID(BigInteger.valueOf(getID()));
			glava.setNaziv(nazivGlave);
			deo.getGlava().add(glava);
		}
		
		
		clan.setID(BigInteger.valueOf(getID()));
		clan.setNaziv(nazivClana);
		clan.setOpis(opisClana);
		clan.setSadrzaj(sadrzaj);
		
		if(nazivGlave.equals(""))
		{
			deo.getClan().add(clan);
		}
		
		
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		Propisi propisi = (Propisi) unmarshall(new File("./data/xml/propisi.xml"));
		
		
		
		deo.setID(BigInteger.valueOf(getID()));
		deo.setNaziv(nazivDela);
		propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().add(deo);

		
		return propisi;
	}

	@Override
	public Propisi dodajGlavu(String requestData) throws JAXBException {
		// TODO Auto-generated method stub
		
		 
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		Propisi propisi = (Propisi) unmarshall(new File("./data/xml/propisi.xml"));
		

		JSONObject json = new JSONObject(requestData);

		String tekstClana = "";
		String redniBroj = "";
		String tekstStava = "";
		String nazivGlave = "";
		
		String nazivClana = json.getString("nazivClana");
		String opisClana = json.getString("opisClana");
		try
		{
			tekstClana = json.getString("tekstClana");
			
		}
		catch(org.json.JSONException e)
		{
			//e.printStackTrace();
		}
		try
		{
			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");
		}
		catch(org.json.JSONException e)
		{
			//e.printStackTrace();
		}
		try
		{
			nazivGlave = json.getString("nazivGlave");
		}
		catch(org.json.JSONException e)
		{
			
		}
		
		Glava glava = new Glava();
		Clan clan = new Clan();
		Sadrzaj sadrzaj = new Sadrzaj();
		Stav stav = new Stav();
		
		if(!redniBroj.equals(""))
		{
			stav.setRedniBroj(Long.parseLong(redniBroj));
			stav.setTekst(tekstStava);
			sadrzaj.getStav().add(stav);
		}
		
		if(!tekstClana.equals(""))
		{
			sadrzaj.getTekst().add(tekstClana);
		}
		
		if(!nazivGlave.equals(""))
		{
			glava.getClan().add(clan);
			glava.setID(BigInteger.valueOf(getID()));
			glava.setNaziv(nazivGlave);
			int brDelova = propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().size();
			propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().add(glava);
		}
		
		
		clan.setID(BigInteger.valueOf(getID()));
		clan.setNaziv(nazivClana);
		clan.setOpis(opisClana);
		clan.setSadrzaj(sadrzaj);
		
		return propisi;
	}

	@Override
	public Propisi dodajClan(String requestData) throws JAXBException {
		
		
		 
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		Propisi propisi = (Propisi) unmarshall(new File("./data/xml/propisi.xml"));

		JSONObject json = new JSONObject(requestData);

		String tekstClana = "";
		String redniBroj = "";
		String tekstStava = "";
		
		String nazivClana = json.getString("nazivClana");
		String opisClana = json.getString("opisClana");
		
		try{
			
			tekstClana = json.getString("tekstClana");
			
		}catch(org.json.JSONException e){
			//e.printStackTrace();
		}
		
		try{
			
			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");
			
		}catch(org.json.JSONException e){
			//e.printStackTrace();
		}
		
		Clan clan = new Clan();
		Sadrzaj sadrzaj = new Sadrzaj();
		Stav stav = new Stav();
		
		if(!redniBroj.equals(""))
		{
			stav.setRedniBroj(Long.parseLong(redniBroj));
			stav.setTekst(tekstStava);
			sadrzaj.getStav().add(stav);
		}
		if(!tekstClana.equals(""))
		{
			sadrzaj.getTekst().add(tekstClana);
		}
		
		int brDelova = propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().size();
		int brGlava = propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().size();
		
		if(brGlava != 0 && propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava() != null ){
			clan.setID(BigInteger.valueOf(getID()));
			clan.setNaziv(nazivClana);
			clan.setOpis(opisClana);
			clan.setSadrzaj(sadrzaj);
			propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().get(brGlava-1).getClan().add(clan);
		}else {
			clan.setID(BigInteger.valueOf(getID()));
			clan.setNaziv(nazivClana);
			clan.setOpis(opisClana);
			clan.setSadrzaj(sadrzaj);
			propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getClan().add(clan);
		}
				
		return propisi;
	}

	@Override
	public Propisi dodajStav(String requestData) throws JAXBException {
		 
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		Propisi propisi = (Propisi) unmarshall(new File("./data/xml/propisi.xml"));	

		JSONObject json = new JSONObject(requestData);


		String redniBroj = "";
		String tekstStava = "";		
		
		try{
			
			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");
			
		}catch(org.json.JSONException e){
			//e.printStackTrace();
		}

		Stav stav = new Stav();
		try {
			Long rbrStava = Long.parseLong(redniBroj);
			if(!redniBroj.equals("")){
				stav.setRedniBroj(rbrStava);
				stav.setTekst(tekstStava);
				int brGlava = 0;
				int brClan = 0;
				int brStav = 0;
				int brDelova = propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().size();
				
				/**
				 * Dodavanje stava na poslednje dodati clan koji u sebi sadrzi stavove
				 */
				if(brDelova != 0){ //provera da li ima delove
					brGlava = propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().size();
					if(brGlava != 0){ //provera da li deo ima glave
						brClan = propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().get(brGlava-1).getClan().size();
						if(brClan != 0){ //provera da li glava ima clanove
							if(propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().get(brGlava-1).getClan().get(brClan-1).getSadrzaj().getStav().size() != 0 
									&& propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().get(brGlava-1).getClan().get(brClan-1).getSadrzaj().getStav() != null){ 
								//provera da li clan ima u sebi stavove, ako ima na njega dodajemo
								propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().get(brGlava-1).getClan().get(brClan-1).getSadrzaj().getStav().add(stav);
							}
						}
					}else{
						brClan = propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova - 1).getClan().size();
						if(brClan != 0){ //provera da li deo ima clanove 
							if(propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getClan().get(brClan-1).getSadrzaj().getStav().size() != 0
									&& propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getClan().get(brClan-1).getSadrzaj().getStav() != null){
								propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getClan().get(brClan-1).getSadrzaj().getStav().add(stav);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		return propisi;
	}



	@Override
	public Propisi unmarshall(File f) throws JAXBException {
		// TODO Auto-generated method stub
		
		JAXBContext context = JAXBContext.newInstance(Propisi.class); 
		
		// Unmarshaller je objekat zadužen za konverziju iz XML-a u objektni model
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		
		// Unmarshalling generiše objektni model na osnovu XML fajla
		return (Propisi) unmarshaller.unmarshal(f);
	}

	@Override
	public void marshall(Propisi t, File f) throws JAXBException {
		// TODO Auto-generated method stub
		JAXBContext context = JAXBContext.newInstance(Propisi.class);
		
		Marshaller marshaller = context.createMarshaller();
		
		// Podešavanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Umesto System.out-a, može se koristiti FileOutputStream
	    marshaller.marshal(t, f);
	}

}
