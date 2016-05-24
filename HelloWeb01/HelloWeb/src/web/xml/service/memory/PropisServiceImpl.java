package web.xml.service.memory;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import jaxb.from.xsd.Clan;
import jaxb.from.xsd.Propis;
import jaxb.from.xsd.Clan.Sadrzaj;
import jaxb.from.xsd.Clan.Sadrzaj.Stav;
import jaxb.from.xsd.Propis.Deo;
import jaxb.from.xsd.Propis.Deo.Glava;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import web.xml.service.PropisService;

@Service
public class PropisServiceImpl implements PropisService
{

	@Override
	public Propis findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Propis> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Propis save(Propis t) {
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
			glava.setID(BigInteger.valueOf(deo.getGlava().size()+1));
			glava.setNaziv(nazivGlave);
			deo.getGlava().add(glava);
		}
		
		
		clan.setID(BigInteger.valueOf(glava.getClan().size()+1));
		clan.setNaziv(nazivClana);
		clan.setOpis(opisClana);
		clan.setSadrzaj(sadrzaj);
		
		if(nazivGlave.equals(""))
		{
			deo.getClan().add(clan);
		}
		
		deo.setID(BigInteger.valueOf(propis.getDeo().size()+1));
		deo.setNaziv(nazivDela);
		
		
		propis.setID(BigInteger.valueOf(1));
		propis.setNaziv(propisNaziv);
		propis.getDeo().add(deo);
		
		return propis;
	}

	@Override
	public Propis dodajDeo(String requestData) throws JAXBException {
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
			glava.setID(BigInteger.valueOf(deo.getGlava().size()+1));
			glava.setNaziv(nazivGlave);
			deo.getGlava().add(glava);
		}
		
		
		clan.setID(BigInteger.valueOf(glava.getClan().size()+1));
		clan.setNaziv(nazivClana);
		clan.setOpis(opisClana);
		clan.setSadrzaj(sadrzaj);
		
		if(nazivGlave.equals(""))
		{
			deo.getClan().add(clan);
		}
		
		
		
		JAXBContext context = JAXBContext.newInstance(Propis.class);
		
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		 
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		jaxb.from.xsd.Propis propis = (jaxb.from.xsd.Propis) unmarshaller.unmarshal(new File("./data/xml/probaPropis.xml"));
	
		deo.setID(BigInteger.valueOf(propis.getDeo().size()+1));
		deo.setNaziv(nazivDela);
		
		propis.getDeo().add(deo);
		
		return propis;
	}

	@Override
	public Propis dodajGlavu(String requestData) throws JAXBException {
		// TODO Auto-generated method stub
		
		JAXBContext context = JAXBContext.newInstance(Propis.class);
		
		Unmarshaller unmarshalle2r = context.createUnmarshaller(); 
		 
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		jaxb.from.xsd.Propis propis = (jaxb.from.xsd.Propis) unmarshalle2r.unmarshal(new File("./data/xml/probaPropis.xml"));
		
		
		
		
		
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
			glava.setID(BigInteger.valueOf(propis.getDeo().get(0).getGlava().size()+1));
			glava.setNaziv(nazivGlave);
			propis.getDeo().get(0).getGlava().add(glava);
		}
		
		
		clan.setID(BigInteger.valueOf(glava.getClan().size()+1));
		clan.setNaziv(nazivClana);
		clan.setOpis(opisClana);
		clan.setSadrzaj(sadrzaj);
		
		if(nazivGlave.equals(""))
		{
			propis.getDeo().get(0).getClan().add(clan);
		}
		
		return propis;
	}

	@Override
	public Propis dodajClan(String requestData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Propis dodajStav(String requestData) {
		// TODO Auto-generated method stub
		return null;
	}

}
