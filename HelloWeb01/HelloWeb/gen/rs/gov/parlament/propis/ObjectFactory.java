//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5.1 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.06.13 at 11:54:22 AM CEST 
//


package rs.gov.parlament.propis;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the rs.gov.parlament.propis package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: rs.gov.parlament.propis
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Propis }
     * 
     */
    public Propis createPropis() {
        return new Propis();
    }

    /**
     * Create an instance of {@link Propis.Deo }
     * 
     */
    public Propis.Deo createPropisDeo() {
        return new Propis.Deo();
    }

    /**
     * Create an instance of {@link Propis.DatumKreiranja }
     * 
     */
    public Propis.DatumKreiranja createPropisDatumKreiranja() {
        return new Propis.DatumKreiranja();
    }

    /**
     * Create an instance of {@link Propis.DatumUsvajanja }
     * 
     */
    public Propis.DatumUsvajanja createPropisDatumUsvajanja() {
        return new Propis.DatumUsvajanja();
    }

    /**
     * Create an instance of {@link Propis.Deo.Glava }
     * 
     */
    public Propis.Deo.Glava createPropisDeoGlava() {
        return new Propis.Deo.Glava();
    }

}
