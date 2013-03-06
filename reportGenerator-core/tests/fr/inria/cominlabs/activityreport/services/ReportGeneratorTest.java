package fr.inria.cominlabs.activityreport.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
	}

	@After
	public void tearDown() throws Exception {
		File file = new File(title);
		file.delete();
	}

	@Test
	public void test()  {
		File file = new File(title);
		assertTrue("Testing web service",ReportGenerator.getInfo()==null);
		assertFalse("Testing web service",ReportGenerator.processHeaderDocument(file)==null);
		
		//fail("Not yet implemented");
	}

}
