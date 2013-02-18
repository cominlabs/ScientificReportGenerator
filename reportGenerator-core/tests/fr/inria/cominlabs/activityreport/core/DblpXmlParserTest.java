package fr.inria.cominlabs.activityreport.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.jdom2.JDOMException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DblpXmlParserTest {
 
	 private String author1Url;
	 private String author2Url;
	 private  String author3Url;
	 private  String author1XmlUrl;
	 private  String author2XmlUrl;
	 private  String author3XmlUrl;
	 private  String myTestFilename; 
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		author1Url = "http://www.informatik.uni-trier.de/~ley/pers/hd/b/Benveniste:Albert.html";
		author2Url = "http://www.informatik.uni-trier.de/~ley/pers/hd/k/Kermarrec:Anne=Marie.html";
		author3Url = "http://www.informatik.uni-trier.de/~ley/pers/hd/f/Frey:Davide.html";
		author1XmlUrl = "http://dblp.uni-trier.de/pers/xx/b/Benveniste:Albert";
		author2XmlUrl = "http://dblp.uni-trier.de/pers/xx/k/Kermarrec:Anne=Marie";
		author3XmlUrl = "http://dblp.uni-trier.de/pers/xx/f/Frey:Davide";
		myTestFilename ="testingfile";
		DblpXmlParser.storeToXmlFile(myTestFilename, author2Url);
		
		
	}

	@After
	public void tearDown() throws Exception {
		File file = new File(myTestFilename);
		file.delete();
	}

	@Test
	public void test() {
		
		assertTrue("Testing method getXmlUrl for author 1", author1XmlUrl.equals(DblpXmlParser.getXmlUrl(author1Url)));
		assertTrue("Testing method getXmlUrl for author 2", author2XmlUrl.equals(DblpXmlParser.getXmlUrl(author2Url)));
		assertTrue("Testing method getXmlUrl for author 3", author3XmlUrl.equals(DblpXmlParser.getXmlUrl(author3Url)));
		
		assertTrue("Testing if the xml file is created correctly", (new File(myTestFilename)).exists());
		try {
			assertFalse("Testing the XML parsing",(DblpXmlParser.xmlFileParser(author2Url, 2012)).isEmpty());
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
