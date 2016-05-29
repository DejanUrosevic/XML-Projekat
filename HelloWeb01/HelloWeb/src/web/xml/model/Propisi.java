package web.xml.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import jaxb.from.xsd.Propis;

@XmlRootElement(name = "propisi")
public class Propisi {
	private List<Propis> propisi = new ArrayList<Propis>();

	public Propisi() {
		// TODO Auto-generated constructor stub
	}

	@XmlElement(name = "propis")
	public List<Propis> getPropisi() {
		return propisi;
	}

	public void setPropisi(List<Propis> propisi) {
		this.propisi = propisi;
	}

}
