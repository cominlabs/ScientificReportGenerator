package fr.inria.cominlabs.activityreport.googlesearch;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author william Kokou Dédzoé
 *
 */
public class GoogleSearchResultUrl {
	
	private static final Logger logger = LoggerFactory.getLogger(GoogleSearchResultUrl.class);
	
	/**
	 *  This method returns the result URL of Google search results set items.
	 *  @param title is object of type java.io.String representing the request.
	 *  @return Object of type java.lang.String representing the URL of page of the a result.
	 */
	
	public static String getResultUrl(String title) throws IOException{
		String url_title = "";
		String default_title_url = "";
		GoogleResults results=GoogleSearch.googleSearchResults(title);
		if ((results !=null) && (results.getResponseData()!=null)){
		for(int i=0;i<=4;i++){
			//String search_title=results.getResponseData().getResults().get(i).getTitle();
			//search_title=search_title.replaceAll("<.*?>", "").trim();
			//if(title.equals(search_title)){
			url_title=results.getResponseData().getResults().get(i).getUrl();
			System.out.println("URL xxxx : " + url_title );
			URL url = new URL(url_title);
			url.openConnection();
			 String new_url=url.toExternalForm();
			 System.out.println("URL yyyy : " + new_url);
			 if (new_url.endsWith(".pdf")){
				 return new_url;
			 }
			//}
			//System.out.println("titre : "+search_title);
			logger.info("url found on google search : "+url_title);
		}
		}
	
		return default_title_url;
		
	}
	
	
}
