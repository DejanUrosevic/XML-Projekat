//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5.1 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.06.03 at 06:48:24 PM CEST 
//


package jaxb.from.xsd;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="Opis" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Naziv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Sadrzaj">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Stav" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Redni_broj" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *                             &lt;element name="Tekst" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Tekst" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="ID" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "opis",
    "naziv",
    "sadrzaj"
})
@XmlRootElement(name = "Clan", namespace = "http://www.parlament.gov.rs/clan")
public class Clan {

    @XmlElement(name = "Opis", namespace = "http://www.parlament.gov.rs/clan")
    protected String opis;
    @XmlElement(name = "Naziv", namespace = "http://www.parlament.gov.rs/clan", required = true)
    protected String naziv;
    @XmlElement(name = "Sadrzaj", namespace = "http://www.parlament.gov.rs/clan", required = true)
    protected Clan.Sadrzaj sadrzaj;
    @XmlAttribute(name = "ID", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger id;

    /**
     * Gets the value of the opis property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpis() {
        return opis;
    }

    /**
     * Sets the value of the opis property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpis(String value) {
        this.opis = value;
    }

    /**
     * Gets the value of the naziv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNaziv() {
        return naziv;
    }

    /**
     * Sets the value of the naziv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNaziv(String value) {
        this.naziv = value;
    }

    /**
     * Gets the value of the sadrzaj property.
     * 
     * @return
     *     possible object is
     *     {@link Clan.Sadrzaj }
     *     
     */
    public Clan.Sadrzaj getSadrzaj() {
        return sadrzaj;
    }

    /**
     * Sets the value of the sadrzaj property.
     * 
     * @param value
     *     allowed object is
     *     {@link Clan.Sadrzaj }
     *     
     */
    public void setSadrzaj(Clan.Sadrzaj value) {
        this.sadrzaj = value;
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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Stav" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Redni_broj" type="{http://www.w3.org/2001/XMLSchema}long"/>
     *                   &lt;element name="Tekst" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Tekst" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "stav",
        "tekst"
    })
    public static class Sadrzaj {

        @XmlElement(name = "Stav", namespace = "http://www.parlament.gov.rs/clan")
        protected List<Clan.Sadrzaj.Stav> stav;
        @XmlElement(name = "Tekst", namespace = "http://www.parlament.gov.rs/clan")
        protected List<String> tekst;

        /**
         * Gets the value of the stav property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the stav property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getStav().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Clan.Sadrzaj.Stav }
         * 
         * 
         */
        public List<Clan.Sadrzaj.Stav> getStav() {
            if (stav == null) {
                stav = new ArrayList<Clan.Sadrzaj.Stav>();
            }
            return this.stav;
        }

        /**
         * Gets the value of the tekst property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the tekst property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTekst().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getTekst() {
            if (tekst == null) {
                tekst = new ArrayList<String>();
            }
            return this.tekst;
        }


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
         *         &lt;element name="Redni_broj" type="{http://www.w3.org/2001/XMLSchema}long"/>
         *         &lt;element name="Tekst" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "redniBroj",
            "tekst"
        })
        public static class Stav {

            @XmlElement(name = "Redni_broj", namespace = "http://www.parlament.gov.rs/clan")
            protected long redniBroj;
            @XmlElement(name = "Tekst", namespace = "http://www.parlament.gov.rs/clan", required = true)
            protected String tekst;

            /**
             * Gets the value of the redniBroj property.
             * 
             */
            public long getRedniBroj() {
                return redniBroj;
            }

            /**
             * Sets the value of the redniBroj property.
             * 
             */
            public void setRedniBroj(long value) {
                this.redniBroj = value;
            }

            /**
             * Gets the value of the tekst property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTekst() {
                return tekst;
            }

            /**
             * Sets the value of the tekst property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTekst(String value) {
                this.tekst = value;
            }

        }

    }

}
