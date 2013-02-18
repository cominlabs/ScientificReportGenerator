package fr.inria.cominlabs.activityreport.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.TreeMap;

public class DBLPHelper {
	

		  private final static String DBLP_SEARCH = "http://dblp.uni-trier.de/search/author?author=";

		  /**
		   * Connects to the official DBLP site, fetches the HTML document for a
		   * specified search request (first name + last name) and returns a TreeMap
		   * with names of persons mapped to their DBLP links
		   * 
		   * @param firstName
		   * @param lastName
		   * @return
		   */
		  public static TreeMap<String, String> fetchByName(String firstName, String lastName) {

		    try {
		      // e.g. url =
		      // http://dblp.uni-trier.de/search/author?author=wolfgang+estgfaeller";
		      URL url = new URL(DBLP_SEARCH + firstName + "+" + lastName);
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

		  private static TreeMap<String, String> filterResult(InputStream is, URL url) {
		    TreeMap<String, String> result = new TreeMap<String, String>();

		    // read from URL
		    BufferedReader br = new BufferedReader(new InputStreamReader(is));
		    String input;
		    try {
		      // filter lines that start with <li> and end with </li>
		      while ((input = br.readLine()) != null) {
		        if (input.startsWith("<html><head><title>DBLP:")) {
		          result.put("READYLINK", url.toString());
		          break;
		        }
		        if (input.startsWith("<li>") && input.endsWith("</li>")) {
		          // split the line and put the name and the link in the
		          // TreeMap
		          String[] temp = input.split("<li><a href=\"|\">|</a></li>");
		          result.put(temp[1], temp[2]);
		        }
		      }
		      return result;
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		    return null;
		  }

}
