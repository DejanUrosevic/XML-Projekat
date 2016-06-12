package web.xml.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;

/**
 * Ekstrakcija RDFa metapodataka iz XML dokumenta primenom GRDDL transformacije.
 * 
 * @author Drazen
 */
public class MetadataExtractor {

	private TransformerFactory transformerFactory;
	private static final String XSLT_FILE = "data/xslt/grddl.xsl";

	public MetadataExtractor() {

		transformerFactory = new TransformerFactoryImpl();

	}

	/**
	 * Generiše RDF/XML na osnovu RDFa metapodataka iz XML koji dolazi ulaznim
	 * strimom. Primenjuje se GRDDL XSL transformacija.
	 * 
	 * @param in ulazni strim koji donosi XML 
	 * @param out izlazni strim koji daje RDF/XML
	 * @throws TransformerException
	 */
	public void extractMetadata(InputStream in, OutputStream out) throws TransformerException {
		
		StreamSource transformSource = new StreamSource(new File(XSLT_FILE));

		Transformer grddlTransformer = transformerFactory.newTransformer(transformSource);

		grddlTransformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
		grddlTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StreamSource source = new StreamSource(in);
		StreamResult result = new StreamResult(out);

		grddlTransformer.transform(source, result);
		
	}

}
