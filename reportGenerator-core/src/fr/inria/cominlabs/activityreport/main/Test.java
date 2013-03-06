package fr.inria.cominlabs.activityreport.main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.List;

import org.jdom2.JDOMException;

import fr.inria.cominlabs.activityreport.articlesdownloader.DownloadPdfFiles;
import fr.inria.cominlabs.activityreport.core.DblpXmlParser;
import fr.inria.cominlabs.activityreport.model.Article;
import fr.inria.cominlabs.activityreport.model.Author;

public class Test {
	
	private final static String DBLP_LINK1="http://www.informatik.uni-trier.de/~ley/pers/hd/k/Kermarrec:Anne=Marie.html";
/*	private final static String DBLP_LINK2="http://www.informatik.uni-trier.de/~ley/pers/hd/b/Benveniste:Albert.html";
	private final static String DBLP_LINK3="http://www.informatik.uni-trier.de/~ley/pers/hd/c/Co=uuml=asnon:Bertrand.html";
	private final static String DBLP_LINK4="http://www.informatik.uni-trier.de/~ley/pers/hd/a/Abiteboul:Serge.html";
	private final static String DBLP_LINK5="http://www.informatik.uni-trier.de/~ley/pers/hd/v/Valduriez:Patrick.html";
	private final static String DBLP_LINK6="http://www.informatik.uni-trier.de/~ley/pers/hd/m/Molli:Pascal.html";*/


			public static void main(String[] args) {
				
				try {
					List<Article> articles1 = DownloadPdfFiles.getAllPdfFiles(DblpXmlParser.xmlFileParser(DBLP_LINK1,2012));
					display(articles1);
				/*	List<Article> articles2 = DownloadPdfFiles.getAllPdfFiles(DblpXmlParser.xmlFileParser(DBLP_LINK2,2010));
					List<Article> articles3 = DownloadPdfFiles.getAllPdfFiles(DblpXmlParser.xmlFileParser(DBLP_LINK3,2010));
					List<Article> articles4 = DownloadPdfFiles.getAllPdfFiles(DblpXmlParser.xmlFileParser(DBLP_LINK4,2010));
					List<Article> articles5 = DownloadPdfFiles.getAllPdfFiles(DblpXmlParser.xmlFileParser(DBLP_LINK5,2010));
					List<Article> articles6 = DownloadPdfFiles.getAllPdfFiles(DblpXmlParser.xmlFileParser(DBLP_LINK6,2010));
					display(articles1);
					display(articles2);
					display(articles3);
					display(articles4);
					display(articles5);
					display(articles6);*/
					
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				  
			}

			  
			public static void display (List<Article> articles){
				
				System.out.println("----------------------------------------------------------------------------------");
				System.out.println("Titre                  Url          Year         Summary                Authors");
				for(Article article : articles){
					  String authors="";
					  
					  for(Author author : article.getAuthors()){
						  authors = authors + " " + author.getFirstLastName();
					  }
							  
					
				   System.out.println(article.getTitle() + " " + article.getUrl() + " " + article.getYear() + " "+ article.getSummary() + " " + authors);
			       System.out.println("------------------------------------------------------------------------------------");
					
				}
				
				
			}


}
