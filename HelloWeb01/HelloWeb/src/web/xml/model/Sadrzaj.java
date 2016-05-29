package web.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sadrzaj")
@XmlAccessorType(XmlAccessType.FIELD)
public class Sadrzaj {

	@XmlElements(value = { @XmlElement(name = "tekst", type = TekstSadrzaja.class),
			@XmlElement(name = "stav", type = String.class) })
	private Object sadrzajInfo;

	public Sadrzaj() {

	}

	public Object getSadrzajInfo() {
		return sadrzajInfo;
	}

	public void setSadrzajInfo(Object sadrzajInfo) {
		this.sadrzajInfo = sadrzajInfo;
	}

}
