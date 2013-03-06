package fr.inria.cominlabs.activityreport.articlesdownloader;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DownloaderTest {

	 String title1 ="best position Algorithm";
	 String url1 = "http://www-sop.inria.fr/members/Patrick.Valduriez/pmwiki/Patrick/uploads//Publications/AkbariniaBpaVLDB07.pdf";
	 String title2 = "ftp file testing";
	 String url2 = "ftp://ftp.free.fr/pub/assistance/adp.pdf";
	 String filename;
	  
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
			Downloader.downloadFileFromUrl(url2, title2);	  
	}

	@After
	public void tearDown() throws Exception {
		//File file1 = new File(title1);
		File file2 = new File(title2);
		//file1.delete();
		file2.delete();
	}

	@Test
	public void test() {
		
		//assertTrue("Testing the downloading is correctly done",(new File(title1)).exists());
		assertTrue("Testing the downloading is correctly done",(new File(title2)).exists());
	}
	

 

}
