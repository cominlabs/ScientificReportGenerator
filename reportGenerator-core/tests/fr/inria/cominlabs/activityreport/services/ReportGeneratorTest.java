package fr.inria.cominlabs.activityreport.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

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
	 
	 String title1 ="mytesting";
	 String url1 = "http://hal.archives-ouvertes.fr/docs/00/72/10/69/PDF/bramerie_JSTQE_2010_review_HAL.pdf";
	 String filename1;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {	
	    Downloader.downloadFileFromUrl(url, title);
	    Downloader.downloadFileFromUrl(url1, title1);
	   
	}

	@After
	public void tearDown() throws Exception {
		File file = new File(title);
		File file1 = new File(title1);
		file.delete();
		file1.delete();
	}

	@Test
	public void test()  {
	    File file = new File(title);
	    File file1 = new File(title1);
		assertTrue("Testing web service",ReportGenerator.getInfo()==null);
		assertFalse("Testing web service",ReportGenerator.processHeaderDocument(file)==null);
		assertFalse("Testing web service",ReportGenerator.processFullDocument(file1)==null);

		
		//fail("Not yet implemented");
	}

}
