package net.iyuyue.ushare;

//main dependence
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class infoHandler {
	
	DocumentBuilderFactory builderFactory;
	DocumentBuilder builder;
	
	public infoHandler() {
		builderFactory = DocumentBuilderFactory.newInstance();
		builder = null;
	}
	
	public NodeList paser(String response, String expression) {
		try {
		    builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
		    e.printStackTrace();  
		}

		try {
			// Parsing XML
		    Document xmlDocument = builder.parse(new ByteArrayInputStream(response.getBytes()));

		    // Creating an XPath object
			XPath xPath =  XPathFactory.newInstance().newXPath();

//			System.out.println(expression);

			// to parse the XML
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
			return nodeList;
//			for (int i = 0; i < nodeList.getLength(); i++) {
//			    System.out.println(nodeList.item(i).getFirstChild().getNodeValue()); 
//			}
		} catch (XPathExpressionException e) {
	        e.printStackTrace();
	    } catch (SAXException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		return null;
		
	}
	
	
	
}
