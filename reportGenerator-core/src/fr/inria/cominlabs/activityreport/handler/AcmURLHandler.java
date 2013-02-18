package fr.inria.cominlabs.activityreport.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author william Kokou D�dzo�
 *
 */

public class AcmURLHandler implements URLHandler{
	
	private final Logger logger = LoggerFactory.getLogger(AcmURLHandler.class);

	
	/**
	 *  This method returns the download link of a given ACM article 
	 *  @param targetUrl is the URL found in DBLP page indicating where to download the article
	 *  @return Object of type java.lang.String containing the URL page where to download the article
	 */
	public String get_download_link(String targetUrl) {
		
		    try {
			      URL url = new URL(targetUrl);
			      URLConnection urlcon = url.openConnection();      
			      return filterResult(urlcon.getInputStream(), url);
			    } catch (MalformedURLException e) {
			      e.printStackTrace();
			      return null;
			    } catch (IOException e) {
			      e.printStackTrace();
			      return null;
			    }


}
	

	/**
	 *  This method returns the pdf download link of a given ACM article 
	 *  @param is is object of type java.io.InputStream representing the contain of a page
	 *  @param url is object of type java.io.String representing the URL of that page
	 *  @return Object of type java.lang.String containing link where to download the pdf of the article
	 */

	private String filterResult(InputStream is, URL url) {
		
		
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
	    String motif="href=\"ft_gateway.cfm.*?\"";
	    Pattern pat=Pattern.compile(motif);
	    String input;
	    String pdf_url=null;
	   logger.info("--------------------------------------------------------------------------------------");
	    try {
			while ((input = br.readLine()) != null) { 
				logger.info(input);
				  Matcher m=pat.matcher(input);
				  if(m.find()){
					  pdf_url= m.group();
					  pdf_url=pdf_url.replaceAll("href=\"", "");
					  pdf_url=pdf_url.replaceAll("\"", "");
					  pdf_url = "http://portal.acm.org/"+pdf_url;
					  logger.info("OK  " + pdf_url);
					 
				  }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
		return pdf_url;
	}
	
}
