package web.xml.iasgns.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import web.xml.iasgns.model.Propis;
import web.xml.iasgns.service.IasgnsService;


@Controller
@RequestMapping("/iasgns")
public class IasgnsController 
{
	
	@Autowired
	IasgnsService iasgnsSer;
	
	@RequestMapping(value = "/propis/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Propis> savePropisToDatabase(@PathVariable(value = "id") String id)
			throws IOException, JAXBException, TransformerConfigurationException, ParserConfigurationException,
			SAXException, TransformerFactoryConfigurationError, TransformerException {

		try
		{
			iasgnsSer.sendToDatabase(id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<Propis>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		return new ResponseEntity<Propis>(HttpStatus.OK);

	}
	@RequestMapping(value = "/naziv/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Propis> getPropisNaziv(@PathVariable(value = "id") String id)
			throws IOException, JAXBException, TransformerConfigurationException, ParserConfigurationException,
			SAXException, TransformerFactoryConfigurationError, TransformerException {

		Propis propis = new Propis();
		propis.setNaziv(id);
		return new ResponseEntity<Propis>(propis, HttpStatus.OK);

	}
}
