package fr.inria.cominlabs.activityreport.articlesdownloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Downloader {
   
	private static final Logger logger = LoggerFactory.getLogger(Downloader.class);
	
	
	public static boolean downloadFileFromUrl(String url,String title) {
		 boolean status = false;
		    try {
			URLConnection con = new URL(url).openConnection();
			 BufferedInputStream in = new BufferedInputStream(con.getInputStream());
			 logger.info("Resource is accesible...");
			 FileOutputStream out = new FileOutputStream(title);
			  int i = 0;
			    byte[] bytesIn = new byte[1024];
			    while ((i = in.read(bytesIn)) >= 0) {
			        out.write(bytesIn, 0, i);
			    }
			    out.close();
			    in.close();
			    status = true;
			    logger.info("File downloaded.");
		    }
		    catch (Exception e) {
		      
		    	 status = false;
		    	 logger.info("Resource is not accesible...");
		    }
		    
		    return status;
		  }
	 
		
	
	public static void downloadFileFromUrl1(String url,String title) throws IOException,MalformedURLException,FileNotFoundException{
		
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
		    public X509Certificate[] getAcceptedIssuers(){return null;}
		    public void checkClientTrusted(X509Certificate[] certs, String authType){}
		    public void checkServerTrusted(X509Certificate[] certs, String authType){}
		}};

		// Install the all-trusting trust manager
		try {
		    SSLContext sc = SSLContext.getInstance("TLS");
		    sc.init(null, trustAllCerts, new SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		    ;
		}
		
		if(!url.equals("")){
		     logger.info("checking de URL ....." + url);
		    //URL file_url  = new URL(null, url,new sun.net.www.protocol.https.Handler());
		     HttpURLConnection.setFollowRedirects(true);
		     HttpURLConnection con =(HttpURLConnection) new URL(url).openConnection();
		     con.setRequestMethod("HEAD");
		      if(con.getResponseCode()==HttpURLConnection.HTTP_OK){
		    	 URL file_url = new URL(url);
				    
			      String new_url=file_url.toExternalForm();
	         
	               File destination = new File(title);
	     
	                FileUtils.copyURLToFile(new URL(new_url), destination);
			 }
		     }
		   
	}
	
	public static void genericDownloadFileFromUrl(String url,String title) throws IOException{
		
		if (reourceIsAccesible(url)){
			
		 URL file_url = new URL(url);
		 URLConnection con = file_url.openConnection();
		 BufferedInputStream in = new BufferedInputStream(con.getInputStream());
		 logger.info("Downloading file.");
		 FileOutputStream out = new FileOutputStream(title);
		  int i = 0;
		    byte[] bytesIn = new byte[1024];
		    while ((i = in.read(bytesIn)) >= 0) {
		        out.write(bytesIn, 0, i);
		    }
		    out.close();
		    in.close();
		    logger.info("http response statuts.." );
		 
		    logger.info("File downloaded.");
		 
		}

	}
	
	 public static boolean exists(String URLName){
		    try {
		    	
		      logger.info("Exist debugging");
		      HttpURLConnection.setFollowRedirects(true);
		      
		      // note : you may also need
		      //        HttpURLConnection.setInstanceFollowRedirects(false)
		      HttpURLConnection con =(HttpURLConnection) new URL(URLName).openConnection();
		      //HttpURLConnection con =(HttpURLConnection) new URL(null, URLName,new sun.net.www.protocol.https.Handler()).openConnection();
		 
		      con.setRequestMethod("HEAD");
		      return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		    }
		    catch (Exception e) {
		       e.printStackTrace();
		       return false;
		    }
		  }
	 
	 public static boolean reourceIsAccesible(String URLName){
		 boolean status = false;
		    try {
			URLConnection con = new URL(URLName).openConnection();
		       con .getInputStream();
		       status = true;
		       logger.info("Resource is accesible...");
		    
		    }
		    catch (Exception e) {
		      
		    	status = false;
		    	 logger.info("Resource is not accesible...");
		    }
		    
		    return status;
		  }
	 
	  
		
}
