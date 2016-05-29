package web.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tekst")
@XmlAccessorType(XmlAccessType.FIELD)
public class TekstSadrzaja {

	@XmlElements(value = { @XmlElement(name = "neki_tekst", type = String.class),
			@XmlElement(name = "refClan", type = String.class) })
	private String sadrzajTeksta;

	public String getSadrzajTeksta() {
		return sadrzajTeksta;
	}

	public void setSadrzajTeksta(String sadrzajTeksta) {
		this.sadrzajTeksta = sadrzajTeksta;
	}

	public TekstSadrzaja() {

	}

}
