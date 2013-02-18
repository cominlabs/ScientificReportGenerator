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


/**
 * @author william Kokou Dédzoé
 *
 */
public class ScienceDirectURLHandler implements URLHandler{

	
	/**
	 *  This method returns the download link of a given ScienceDirect article. 
	 *  @param targetUrl is the URL found in DBLP page indicating where to download the article.
	 *  @return Object of type java.lang.String containing the URL page where to download the article.
	 */
	public String get_download_link(String targetUrl) {
		
		    try {
			      URL url = new URL(targetUrl);
			
			      URLConnection urlcon = url.openConnection(); 
			      System.out.println(url.toExternalForm());
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
	 *  This method returns the pdf download link of a given ScienceDirect article.
	 *  @param is is object of type java.io.InputStream representing the contain of a page.
	 *  @param url is object of type java.io.String representing the URL of that page.
	 *  @return Object of type java.lang.String containing link where to download the pdf of the article.
	 */
	private String filterResult(InputStream is, URL url) {
		 BufferedReader br = new BufferedReader(new InputStreamReader(is));
		 String input;
		 String pdf_url=null;
		 String motif="<a id=\"pdflink\" href=\".*?\"";
		 String begin_text="<a id=\"pdflink\" href=\"";
		 String end_text="\"";
		 Pattern pat=Pattern.compile(motif);
			
			try {
				while ((input = br.readLine()) != null) {
					System.out.println(input);
					Matcher m=pat.matcher(input);
					if (m.find()){
						pdf_url=m.group();
						pdf_url=pdf_url.replaceAll(begin_text,"");
						pdf_url=pdf_url.replaceAll(end_text,"");
						return pdf_url;
					}
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return pdf_url;
	
}
	
	
}
