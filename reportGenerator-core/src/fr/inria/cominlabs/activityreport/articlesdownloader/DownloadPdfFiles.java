package fr.inria.cominlabs.activityreport.articlesdownloader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.inria.cominlabs.activityreport.googlesearch.GoogleSearchResultUrl;
import fr.inria.cominlabs.activityreport.model.Article;
import fr.inria.cominlabs.activityreport.services.ReportGenerator;

public class DownloadPdfFiles {
	static final HashMap<String,String> list_editors =new HashMap<String, String>();
	private final static Logger logger = LoggerFactory.getLogger(DownloadPdfFiles.class);
	
	static {
		list_editors.put("acm", "AcmURLHandler");
		list_editors.put("springer", "SpringerURLHandler");
		list_editors.put("springerlink", "SpringerNewURLHandler");
	}

	
	public static List<Article> getAllPdfFiles(List<Article> articles) throws MalformedURLException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
		Vector<String> search_from_google=new Vector<String>();
		for(Article article : articles){
			System.out.println("Article " + article.getTitle());
			
				search_from_google.addElement(article.getTitle());
		
		/*	if((article.getUrl()!= null)&&article.getUrl().endsWith(".pdf")){
				Downloader.downloadFileFromUrl(article.getUrl(),article.getTitle());
				logger.info("This is direct downloaded link");
			}
	
			else{
				search_from_google.addElement(article.getTitle());
			}*/
		}
		
		ExecutorService downloadExecutorService = Executors.newFixedThreadPool(10);
		List<Callable<Boolean>> downloadCallable = new ArrayList<Callable<Boolean>>();
		
		for(int i=0;i<search_from_google.size();i++){
			String element=search_from_google.get(i); 
			//searchCallable.add(new GoogleSearchResultUrl(element));
			  String s=GoogleSearchResultUrl.getResultUrl(element);
			  downloadCallable.add(new Downloader(s,element));
				//Downloader.downloadFileFromUrl(s,element);
			    logger.info("This is downloaded using Google");
			 
		}
		
		List<Future<Boolean>> downloadResults = new ArrayList<Future<Boolean>>();
		try {
			 downloadResults = downloadExecutorService.invokeAll(downloadCallable);
			 for(Future<Boolean> f:downloadResults){
				 logger.info("File downloaded or not " + f.get().booleanValue());
			 }
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ReportGenerator.getArticlesWithSummaries(articles);
	}
	
	public static String getPaperEditor(String url) throws IOException{
		  URL correct_url = new URL(url);
	     @SuppressWarnings("unused")
		URLConnection urlcon = correct_url.openConnection(); 
	      String target_url=correct_url.toExternalForm();
	      
	      for(String key :list_editors.keySet()){
	    	  Pattern pat=Pattern.compile(key);
	    	  Matcher m = pat.matcher(target_url);
	    	  if(m.find()){
		    	  return list_editors.get(key);
		      }  
	      }
	     
	      return null;
	}

}
