package fr.inria.cominlabs.activityreport.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.multipart.FormDataMultiPart;

import fr.inria.cominlabs.activityreport.core.TeiParser;
import fr.inria.cominlabs.activityreport.model.Article;

@SuppressWarnings("unused")
public class ReportGenerator {

	private static final Logger logger = LoggerFactory.getLogger(ReportGenerator.class);
	
	private final static String BASE_SERVICE_URL="http://localhost:8080/grobid-service-0.2.2";
	
	public static String getInfo() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		URI uri = UriBuilder.fromUri(BASE_SERVICE_URL).build();
		WebResource service = client.resource(uri);
		logger.info(service.path("grobid").accept(MediaType.TEXT_HTML).get(String.class));
		
		
		return null;
	}
	
	public static String processHeaderDocument(File pdfFile)  {
	
		String summary = "";
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		URI uri = UriBuilder.fromUri(BASE_SERVICE_URL).build();
		WebResource service = client.resource(uri);
		FormDataMultiPart form = new FormDataMultiPart();
		//processFulltextDocument
		form.field("filecontent", pdfFile, MediaType.MULTIPART_FORM_DATA_TYPE);
		ClientResponse response = service.path("processHeaderDocument").type(MediaType.MULTIPART_FORM_DATA_TYPE)
				                         .accept(MediaType.APPLICATION_XML)
				                         .post(ClientResponse.class,form);
		InputStream inputStream = response.getEntityInputStream();	
		if(response.getClientResponseStatus().getStatusCode()==200) {
			
			 	//TeiParser.retrieveAbstact(inputStream);
     	try {
					summary = getArticleAbstract(inputStream);
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			
			
		}
		
		
		
		return summary;
	}
	
	private static String getArticleAbstract(InputStream inputStream) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		String articleAbstract = "";
		Document document = (Document) builder.build(inputStream);
		
		    if (document.getContentSize()>0) {
		    	Element rootNode = document.getRootElement();
				//logger.info("Root element of XML document is : " + rootNode.getName());
				 ElementFilter filter=new org.jdom2.filter.ElementFilter("p");
				 for(Element c:rootNode.getDescendants(filter))
				  {
					   articleAbstract = c.getTextNormalize();
				    //   logger.info( articleAbstract);
				  }
		    }
				
		return articleAbstract;
	}
	

	
public static List<Article> getArticlesWithSummaries(List<Article> articles){
		
		for(Article article : articles){
			
			File file = new File(article.getTitle());
			if (file.exists()){
				String summary = processHeaderDocument(file);
				article.setSummary(summary);
				}			
		}
		
		return articles;
		
	}
}
