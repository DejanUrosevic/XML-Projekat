//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5.1 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.06.03 at 03:54:34 PM CEST 
//


package jaxb.from.xsd;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.parlament.gov.rs/propis}Propis"/>
 *         &lt;element name="Resenje" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Obrazlozenje" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{http://www.parlament.gov.rs/clan}Clan"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Datum" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="Mesto" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Redni_broj" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="Podnosilac" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "propis",
    "resenje",
    "obrazlozenje",
    "clan"
})
@XmlRootElement(name = "Amandman", namespace = "http://www.parlament.gov.rs/amandman")
public class Amandman {

    @XmlElement(name = "Propis", required = true)
    protected Propis propis;
    @XmlElement(name = "Resenje", namespace = "http://www.parlament.gov.rs/amandman", required = true)
    protected String resenje;
    @XmlElement(name = "Obrazlozenje", namespace = "http://www.parlament.gov.rs/amandman", required = true)
    protected String obrazlozenje;
    @XmlElement(name = "Clan", namespace = "http://www.parlament.gov.rs/clan", required = true)
    protected Clan clan;
    @XmlAttribute(name = "Datum")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar datum;
    @XmlAttribute(name = "Mesto")
    protected String mesto;
    @XmlAttribute(name = "Redni_broj")
    protected Integer redniBroj;
    @XmlAttribute(name = "ID")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger id;
    @XmlAttribute(name = "Podnosilac")
    protected String podnosilac;

    /**
     * Gets the value of the propis property.
     * 
     * @return
     *     possible object is
     *     {@link Propis }
     *     
     */
    public Propis getPropis() {
        return propis;
    }

    /**
     * Sets the value of the propis property.
     * 
     * @param value
     *     allowed object is
     *     {@link Propis }
     *     
     */
    public void setPropis(Propis value) {
        this.propis = value;
    }

    /**
     * Gets the value of the resenje property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResenje() {
        return resenje;
    }

    /**
     * Sets the value of the resenje property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResenje(String value) {
        this.resenje = value;
    }

    /**
     * Gets the value of the obrazlozenje property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObrazlozenje() {
        return obrazlozenje;
    }

    /**
     * Sets the value of the obrazlozenje property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObrazlozenje(String value) {
        this.obrazlozenje = value;
    }

    /**
     * Gets the value of the clan property.
     * 
     * @return
     *     possible object is
     *     {@link Clan }
     *     
     */
    public Clan getClan() {
        return clan;
    }

    /**
     * Sets the value of the clan property.
     * 
     * @param value
     *     allowed object is
     *     {@link Clan }
     *     
     */
    public void setClan(Clan value) {
        this.clan = value;
    }

    /**
     * Gets the value of the datum property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDatum() {
        return datum;
    }

    /**
     * Sets the value of the datum property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDatum(XMLGregorianCalendar value) {
        this.datum = value;
    }

    /**
     * Gets the value of the mesto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMesto() {
        return mesto;
    }

    /**
     * Sets the value of the mesto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMesto(String value) {
        this.mesto = value;
    }

    /**
     * Gets the value of the redniBroj property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRedniBroj() {
        return redniBroj;
    }

    /**
     * Sets the value of the redniBroj property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRedniBroj(Integer value) {
        this.redniBroj = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setID(BigInteger value) {
        this.id = value;
    }

    /**
     * Gets the value of the podnosilac property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPodnosilac() {
        return podnosilac;
    }

    /**
     * Sets the value of the podnosilac property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPodnosilac(String value) {
        this.podnosilac = value;
    }

}
