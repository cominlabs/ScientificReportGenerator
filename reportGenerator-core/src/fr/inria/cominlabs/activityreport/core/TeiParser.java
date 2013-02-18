    package fr.inria.cominlabs.activityreport.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class TeiParser {

	private static final Logger logger = LoggerFactory.getLogger(TeiParser .class);
    
	
	public static String retrieveAbstact(InputStream inputStream) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		String articleAbstract = "";
		Document document = (Document) builder.build(inputStream);
		
		
		
		return null;
	}
}
