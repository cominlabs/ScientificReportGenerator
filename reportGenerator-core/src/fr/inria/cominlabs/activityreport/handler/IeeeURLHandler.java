package fr.inria.cominlabs.activityreport.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;



import fr.inria.cominlabs.activityreport.core.DblpXmlParser;


/**
 * @author william Kokou Dédzoé
 *
 */

public class IeeeURLHandler implements URLHandler{
	public final static String TEMP_FILE="tmp.xml";
	public final static String YOUR_SPRINGER_KEY="9zejr58ffzz5xnnfwsskrckq";

	/**
	 *  This method returns the download link of a given IEEE article. 
	 *  @param targetUrl is the URL found in DBLP page indicating where to download the article.
	 *  @return Object of type java.lang.String containing the URL page where to download the article.
	 */
	public String get_download_link(String targetUrl) {
		
		    try {
			      URL url = new URL(targetUrl);
			      URLConnection urlcon = url.openConnection(); 
			      System.out.println(url.toExternalForm());
			      return filterResult(urlcon.getInputStream(), new URL(url.toExternalForm()));
			    } catch (MalformedURLException e) {
			      e.printStackTrace();
			      return null;
			    } catch (IOException e) {
			      e.printStackTrace();
			      return null;
			    }


}
	/**
	 *  This method returns the pdf download link of a given IEEE article 
	 *  @param is is object of type java.io.InputStream representing the contain of a page
	 *  @param url is object of type java.io.String representing the URL of that page
	 *  @return Object of type java.lang.String containing link where to download the pdf of the article
	 */
	private String filterResult(InputStream is, URL url) {
		// TODO Auto-generated method stub
		//String new_url=url.toExternalForm();
		//String motif="http://ieeexplore.ieee.org";
		 BufferedReader br = new BufferedReader(new InputStreamReader(is));
		 String input;
		 String pdf_url=null;
		 String prefix="http://ieeexplore.ieee.org/stampPDF/getPDF.jsp?tp=&";
		 String motif="arnumber=\\d+";
		 //String motif="<meta name=\"citation_pdf_url\" content=\".*?\">";
		 //String begin_text="<meta name=\"citation_pdf_url\" content=\"";
		 @SuppressWarnings("unused")
		String end_text="\">";
		 Pattern pat=Pattern.compile(motif);
			
			try {
				while ((input = br.readLine()) != null) {
					//System.out.println(input);
					Matcher m=pat.matcher(input);
					if (m.find()){
						pdf_url=m.group();
						//pdf_url=pdf_url.replaceAll(begin_text,"");
						//pdf_url=pdf_url.replaceAll(end_text,"");
						//System.out.println("ok");
						System.out.println(pdf_url);
						pdf_url=prefix+pdf_url;
						return pdf_url;
					}
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return pdf_url;
	
}
	/**
	 *  This method returns the download link of a given IEEE article 
	 *  @param targetUrl is the URL found in DBLP page indicating where to download the article
	 *  @return Object of type java.lang.String containing the URL page where to download the article
	 */
	   public static String getArticleDoi(String url){
	    	 String start_string_1="http://dx.doi.org/";
	    	 String start_string_2="http://doi.ieeecomputersociety.org/";
	    	 String doi=null;
	    	
	    	 if (url.startsWith(start_string_1)){
	    		 doi=url.substring(start_string_1.length());
	    	 }
	    	 else 
		    	 if (url.startsWith(start_string_2)){
		    		 doi=url.substring(start_string_2.length());
		    	 }
	    	 System.out.println("doi ---------" + doi);
	    	 return doi;
	     }
	     
	   
	   /**
		 *  This method the abstract of a given IEEE article 
		 *  @param url is the URL of the article
		 *  @return Object of type java.lang.String containing the abstract of the article
		 */
	     public static String getIeeeAbstract(String url) throws IOException, JDOMException{
	    	 String doi=getArticleDoi(url);
	    	 String abstract_value="";
	    	 String query = "http://ieeexplore.ieee.org/gateway/ipsSearch.jsp?doi=" +  doi +"&hc=1&rs=1";
	    	 System.out.println("query : " + query);
	    	  File file=DblpXmlParser.createFile(TEMP_FILE);
			  FileWriter fileWritter= new FileWriter(file.getName(),true);
			  BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
				URL url_target = new URL(query);
				   URLConnection urlcon = url_target.openConnection();   
				      InputStream is=urlcon.getInputStream();
						// read from URL
					    BufferedReader br = new BufferedReader(new InputStreamReader(is));
					    String input=null;
					    	 //System.out.println ("--------------------------------------------------------------------------------------");
					      while ((input = br.readLine()) != null) {
					    	  System.out.println(input);
					    	  bufferWritter.write(input);
					      }
					      bufferWritter.close();
					      SAXBuilder builder = new SAXBuilder();
						  File xmlFile = new File(TEMP_FILE);
						  Document document = (Document) builder.build(xmlFile);
						  Element rootNode = document.getRootElement();
						 List<Element> list = rootNode.getChildren("document");
						 Element node = list.get(0);
						  abstract_value=node.getChildText("abstract");
						  System.out.println(abstract_value);
						 return abstract_value;	    	 
	     }
	     
	     
	   /*  public static String getSpringerAbstract(String url) throws IOException, JDOMException{
	    	 String doi=getArticleDoi(url);
	    	 String abstract_value="";
	    	 String query ="http://api.springer.com/metadata/pam?q=doi:"+doi+"&api_key="+YOUR_SPRINGER_KEY;
	    	 System.out.println("query : " + query);
				URL url_target = new URL(query);
				   URLConnection urlcon = url_target.openConnection();   
				      InputStream is=urlcon.getInputStream();
						// read from URL
					    BufferedReader br = new BufferedReader(new InputStreamReader(is));
					    String input=null;
					    String motif="<xhtml:body>.*?</xhtml:body>";
					    Pattern pat=Pattern.compile(motif);
					    	 //System.out.println ("--------------------------------------------------------------------------------------");
					      while ((input = br.readLine()) != null) {
					    	 // System.out.println(input);
					    	  Matcher m=pat.matcher(input);
					    	  if (m.find()){
					    		  abstract_value=m.group();
					    		  abstract_value=abstract_value.replaceAll("<xhtml:body>","");
					    		  abstract_value=abstract_value.replaceAll("</xhtml:head>","");
					    		  abstract_value=abstract_value.replaceAll("<h1>Abstract</h1>","");
					    		  abstract_value=abstract_value.replaceAll("<.*?>", "").trim();
					    	  }
					    	
					      }
					   
						  System.out.println(abstract_value);
						 return abstract_value;	    	 
	     }
	     
	     public static String getIeeeComputerSocietyAbstract(String url) throws IOException, JDOMException{
	    	 return  getSpringerAbstract(url);
	     }
	     
	     public static String getAcmDigitalLibraryAbstract(String url)throws IOException, JDOMException{
	    	 String doi = getArticleDoi(url);
	    	 String abstract_value = "";
	    	 String query = "http://dl.acm.org/results.cfm?adv=1&COLL=DL&DL=ACM&termzone=all&allofem=&anyofem=&noneofem=&peoplezone=Name&people=&peoplehow=not&keyword=&keywordhow=NOT&affil=&affilhow=NOT&pubin=&pubinhow=and&pubby=&pubbyhow=NOT&since_year=&before_year=&pubashow=OR&sponsor=&sponsorhow=NOT&confdate=&confdatehow=NOT&confloc=&conflochow=NOT&isbnhow=OR&isbn=&doi=" + 
	    	 doi + "&ccs=&subj=&hasabs=on&Go.x=32&Go.y=9";
	    	 URL url_target = new URL(query);
			   URLConnection urlcon = url_target.openConnection();   
			      InputStream is=urlcon.getInputStream();
					// read from URL
				    BufferedReader br = new BufferedReader(new InputStreamReader(is));
				    String input=null;
	    	 
	    	 
	    	 return null;
	     }*/
}
