package fr.inria.cominlabs.activityreport.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.inria.cominlabs.activityreport.model.Article;
import fr.inria.cominlabs.activityreport.model.Author;

/**
 * This class is DBLP database parser. 
 * @author william Kokou Dédzoé
 *
 */

public class DblpXmlParser {
	private final static String DBLP_XML_LINK="http://dblp.uni-trier.de/pers/xx/";
	private final static String DBLP_URL_START="http://www.informatik.uni-trier.de/~ley/pers/hd/";
	private static final Logger logger = LoggerFactory.getLogger(DblpXmlParser .class);

	  public static void storeToXmlFile(String filename, String dblp_link) throws IOException{
		      String dblp_link_xml=getXmlUrl(dblp_link);
			  File file=createFile(filename);
			  FileWriter fileWritter= new FileWriter(file.getName(),true);
			  BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		      URL url = new URL(dblp_link_xml);
		      String new_url=url.toExternalForm();
		      url=new URL(new_url);
		      URLConnection urlcon = url.openConnection();   
		      InputStream is=urlcon.getInputStream();
				// read from URL
			    BufferedReader br = new BufferedReader(new InputStreamReader(is));
			    String input=null;
			    	 //System.out.println ("--------------------------------------------------------------------------------------");
			      while ((input = br.readLine()) != null) {
			    	  bufferWritter.write(input);
			      }
			      bufferWritter.close();
		 
	  }
	  
	  
	  /**
		 *  This method creates a given name file.
		 *  @param filename is the name of the file to create 
		 *  @param year_of_publication is year from which we want to retrieve the author publications.
		 *  @return Object of type java.io.File representing the file.
		 */
	   public static File createFile(String filename){
		  File file=null;
  try {
			  String real_filename=new String(filename);
		       file = new File(real_filename);
		       
	 
		      if (file.createNewFile()){
		        System.out.println("File is created!");
		      }else{
		    	  file.delete();
		    	  file.createNewFile();
		        System.out.println("File already exists.");
		      }
	 
	    	} catch (IOException e) {
		      e.printStackTrace();
		}
     return file;
		  
	  }
	  
	  
		/**
		 *  This method returns the list of links  to download a given author articles.
		 *  @param filename is  the xml file of a author  publications. 
		 *  @param year_of_publication is year from which we want to retrieve the author publications.
		 *  @return Object of type java.util.List containing a list of articles to download.
		 */
	  public static List<Article> xmlFileParser(String dblp_link, int year_of_publication) throws JDOMException, IOException {
		  
		  String dblp_link_xml=getXmlUrl(dblp_link);
		  URL dblp_link_xml_url = new URL(dblp_link_xml);
		  SAXBuilder builder = new SAXBuilder();
		  //File xmlFile = new File(filename);
		  HashMap<String,String> publi_to_download  =new HashMap<String,String>();
		  String year_of_publi= Integer.toString(year_of_publication);
			Document document = (Document) builder.build( dblp_link_xml_url);
			Element rootNode = document.getRootElement();
			List<Article> articles = new ArrayList<Article>();
			StringBuffer authors1 ;
			logger.info("Root element of XML document is : " + rootNode.getName());
			List<Element> list = rootNode.getChildren("r");
			logger.info("Number of publications : "+ list.size());
			logger.info( "Title                   " + "Year        "  +  "Url") ;
			for (int i = 0; i < list.size(); i++) {
				Article article = new Article();
				List<Author> authors = new ArrayList<Author>();
				   Element node = list.get(i);
				   List<Element> child=node.getChildren();
				   Element correct_child=child.get(0);
				   List<Element> childAuthor = correct_child.getChildren("author");
				   authors1=new StringBuffer();
				   for (int j = 0; j < childAuthor.size(); j++){
					   Author author = new Author(); 
					   author.setFirstLastName(childAuthor.get(j).getText());
					   authors.add(author);
					   authors1 = new StringBuffer(authors1.toString() + " " + childAuthor.get(j).getText());
					   //logger.info("Author  " + i  +  "  " + childAuthor.get(j).getText());
					   
				   }
				   String title = correct_child.getChildText("title");
				    title=title.replaceAll("\\.", "");
				    title=title.replaceAll(",", "");
				    String year = correct_child.getChildText("year");
				    String url = correct_child.getChildText("ee");
				    if(year_of_publi.equals(year)){ 
				    publi_to_download.put(title,url) ;	
				    article.setUrl(url);
				    article.setTitle(title);
				    article.setYear(new Integer(year));
				    article.setAuthors(authors);
				    articles.add(article);
				    logger.info("--------------------------------------------------------------------------------------------------------------------");
				    logger.info(title + "   " + url + "   "  +  authors1.toString() + "    " + year);
				 
				    }
		 
				}
			return articles;
			 	
		  
	  }
	  
	  /**
		 *  This method returns the xml link of a given author DBLP link.
		 *  @param url is object of type java.lang.String representing  the DBLP page link of author. 
		 *  @return Object of type java.lang.String representing the xml link of the author page.
		 */
	  
	  public static String getXmlUrl(String url){
		  String result=null;
		  result=url.substring(DBLP_URL_START.length());
		  result=result.replaceAll("\\.html","");
		  result=DBLP_XML_LINK+result;
		  System.out.println( result);
		  return result;
		  
	  }
	
	  
}
