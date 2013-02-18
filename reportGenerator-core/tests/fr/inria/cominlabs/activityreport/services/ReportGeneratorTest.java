package fr.inria.cominlabs.activityreport.services;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inria.cominlabs.activityreport.articlesdownloader.Downloader;

public class ReportGeneratorTest {
	
	 String title ="best position Algorithm";
	 String url = "http://www-sop.inria.fr/members/Patrick.Valduriez/pmwiki/Patrick/uploads//Publications/AkbariniaBpaVLDB07.pdf";
	 String filename;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		String title ="best position Algorithm";
		String url = "http://www-sop.inria.fr/members/Patrick.Valduriez/pmwiki/Patrick/uploads//Publications/AkbariniaBpaVLDB07.pdf";
		try {
			Downloader.downloadFileFromUrl(url, title);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	}

	@After
	public void tearDown() throws Exception {
		File file = new File(title);
		file.delete();
	}

	@Test
	public void test() {
		File file = new File(title);
		assertTrue("Testing web service",ReportGenerator.getInfo()==null);
		assertTrue("Testing web service",ReportGenerator.processHeaderDocument(file)==null);
		
		//fail("Not yet implemented");
	}

}
