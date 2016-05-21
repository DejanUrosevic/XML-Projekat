package web.xml.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="clanovi")
@XmlAccessorType(XmlAccessType.FIELD)
public class Clanovi {

	private List<Clan> clan = new ArrayList<Clan>();
	
	public Clanovi()
	{
		
	}

	@XmlElement(name="clan")
	public List<Clan> getClan() {
		return clan;
	}

	public void setClanovi(List<Clan> clan) {
		this.clan = clan;
	}
	
	
}
