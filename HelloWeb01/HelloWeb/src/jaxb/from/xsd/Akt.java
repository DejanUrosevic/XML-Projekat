//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5.1 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.06.07 at 02:55:49 PM CEST 
//


package jaxb.from.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *       &lt;choice>
 *         &lt;element ref="{http://www.parlament.gov.rs/propis}Propis"/>
 *         &lt;element ref="{http://www.parlament.gov.rs/amandman}Amandman"/>
 *       &lt;/choice>
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
    "amandman"
})
@XmlRootElement(name = "Akt", namespace = "http://www.parlament.gov.rs/akt")
public class Akt {

    @XmlElement(name = "Propis")
    protected Propis propis;
    @XmlElement(name = "Amandman", namespace = "http://www.parlament.gov.rs/amandman")
    protected Amandman amandman;

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
     * Gets the value of the amandman property.
     * 
     * @return
     *     possible object is
     *     {@link Amandman }
     *     
     */
    public Amandman getAmandman() {
        return amandman;
    }

    /**
     * Sets the value of the amandman property.
     * 
     * @param value
     *     allowed object is
     *     {@link Amandman }
     *     
     */
    public void setAmandman(Amandman value) {
        this.amandman = value;
    }

}
