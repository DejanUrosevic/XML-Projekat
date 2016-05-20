package web.xml.util;

import static org.apache.xerces.jaxp.JAXPConstants.JAXP_SCHEMA_LANGUAGE;
import static org.apache.xerces.jaxp.JAXPConstants.W3C_XML_SCHEMA;

import java.io.File;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DOMParser implements ErrorHandler
{
	
private static DocumentBuilderFactory factory;
	
	private Document document;
	
	/*
	 * Factory initialization static-block
	 */
	static {
		factory = DocumentBuilderFactory.newInstance();
		
		/* Ukljuèuje validaciju. */ 
		factory.setValidating(true);
		
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(true);
		
		/* Validacija u odnosu na XML šemu. */
		factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
	}
	
	/**
	 * Generates document object model for a given XML file.
	 * 
	 * @param filePath XML document file path
	 */
	public void buildDocument(String filePath) {

		try {
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			/* Postavlja error handler. */
			builder.setErrorHandler(this);
			
			document = builder.parse(new File(filePath)); 

			/* Detektuju eventualne greske */
			if (document != null)
				System.out.println("[INFO] File parsed with no errors.");
			else
				System.out.println("[WARN] Document is null.");

		} catch (SAXParseException e) {
			
			System.out.println("[ERROR] Parsing error, line: " + e.getLineNumber() + ", uri: " + e.getSystemId());
			System.out.println("[ERROR] " + e.getMessage() );
			System.out.print("[ERROR] Embedded exception: ");
			
			Exception embeddedException = e;
			if (e.getException() != null)
				embeddedException = e.getException();

			// Print stack trace...
			embeddedException.printStackTrace();
			
			System.exit(0);
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String printElement(String elementName) 
	{
	    Element element = null;
	   
		NodeList nodes = document.getElementsByTagName(elementName);
		
		for (int i = 0; i < nodes.getLength(); i++)
		{
			if(nodes.item(i) instanceof Element)
			{
				element = (Element) nodes.item(i);
			}
		}
		
		NodeList children = element.getChildNodes();
		
		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				Node aChild = children.item(i);
				if(aChild instanceof Text)
				{
					Text text = (Text)aChild;
					return text.getTextContent().trim();
				}
			}
		}
		
		return "Empty";
			
	}

	@Override
	public void error(SAXParseException arg0) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatalError(SAXParseException arg0) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warning(SAXParseException arg0) throws SAXException {
		// TODO Auto-generated method stub
		
	}
}
