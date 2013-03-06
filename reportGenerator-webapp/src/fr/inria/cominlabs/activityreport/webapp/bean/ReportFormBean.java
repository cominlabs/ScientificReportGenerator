package fr.inria.cominlabs.activityreport.webapp.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.jdom2.JDOMException;

import fr.inria.cominlabs.activityreport.articlesdownloader.DownloadPdfFiles;
import fr.inria.cominlabs.activityreport.core.DblpXmlParser;
import fr.inria.cominlabs.activityreport.model.Article;

@ManagedBean(name="ReportFormBean")
@RequestScoped
public class ReportFormBean {
	private UploadedFile file;
	private String filename;
	private int year;
	private List<String> authorsLink = new ArrayList<String>();
	private List<Article> articles = new ArrayList<Article>();
	public String getFilename() {
		return this.filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getYear() {
		return this.year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public UploadedFile getFile() {
		return this.file;
	}
	public void setFile(UploadedFile file) {
		this.file = file;
	}
	
 public List<Article> getArticles() {
		return this.articles;
	}
	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}
public  String submit(){
	 
	 if(file != null){
		 System.out.println("processed " + file.getName() );
		 displayFile();
		 
	 }
	 
	 return "welcome";
 }
 
 public void displayFile(){
	  try {
		BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
		String input=null;
   	 
       while ((input = br.readLine()) != null) {
    	   System.out.println(input);
    	   authorsLink.add(input);
    	   try {
			articles.addAll(DownloadPdfFiles.getAllPdfFiles(DblpXmlParser.xmlFileParser(input.trim(),year)));
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
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
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
 }
}
