package web.xml.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "clan")
@XmlAccessorType(XmlAccessType.FIELD)
public class Clan 
{
	private long id;
	private String opis;
	private String naziv;
	private Sadrzaj sadrzaj;
	private TekstSadrzaja tekstSadrzaja;

	
	public Clan()
	{
		
	}

	@XmlElement(required = true)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@XmlElement(required = false)
	public String getOpis() {
		return opis;
	}

	public void setOpis(String opis) {
		this.opis = opis;
	}

	@XmlElement(required = true)
	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	@XmlElement(required = false)
	public Sadrzaj getSadrzaj() {
		return sadrzaj;
	}

	public void setSadrzaj(Sadrzaj sadrzaj) {
		this.sadrzaj = sadrzaj;
	}

	@XmlElement(required = false)
	public TekstSadrzaja getTekstSadrzaja() {
		return tekstSadrzaja;
	}

	public void setTekstSadrzaja(TekstSadrzaja tekstSadrzaja) {
		this.tekstSadrzaja = tekstSadrzaja;
	}

	
	
	
}
