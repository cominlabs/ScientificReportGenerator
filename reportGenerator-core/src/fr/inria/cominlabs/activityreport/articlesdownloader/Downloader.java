package fr.inria.cominlabs.activityreport.articlesdownloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
	public static void downloadFileFromUrl(String url,String title) throws IOException,MalformedURLException,FileNotFoundException{
		
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
		     URL file_url = new URL(url);
		 
		      String new_url=file_url.toExternalForm();
         
               File destination = new File(title);
     
                FileUtils.copyURLToFile(new URL(new_url), destination);
		}
	}
}
