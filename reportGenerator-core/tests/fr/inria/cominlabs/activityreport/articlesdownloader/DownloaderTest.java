package fr.inria.cominlabs.activityreport.articlesdownloader;

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

public class DownloaderTest {

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
		
		assertTrue("Testing the downloading is correctly done",(new File(title)).exists());
	}
	

 

}
