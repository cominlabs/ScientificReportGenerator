    package fr.inria.cominlabs.activityreport.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("unused")
public class TeiParser {

	private static final Logger logger = LoggerFactory.getLogger(TeiParser.class);
    
	
	public static String retrieveAbstact(InputStream inputStream) {
		 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		 String input;
		 String articleAbstract = "";
		 List<String> abstract_Lignes = new ArrayList<String>();
		 Boolean checker =false;
		 
			try {
				while ((input = br.readLine()) != null) {
					//logger.info("input of the file " + input);
					if (input.trim().startsWith("<div type=\"abstract\"")){
						 System.out.println("OK");
							abstract_Lignes.add(input);
						checker=true;
					}
						if (checker.booleanValue()==true) break;	
					
						}
			} catch (IOException e) {
				// TODO Auto-generated catch blocks
				e.printStackTrace();
			}
			
			for (String s : abstract_Lignes){
				logger.info("lignes " + s);
			}
		return articleAbstract;
	}
}
