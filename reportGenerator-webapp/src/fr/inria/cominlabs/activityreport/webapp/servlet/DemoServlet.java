package fr.inria.cominlabs.activityreport.webapp.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jdom2.JDOMException;

import fr.inria.cominlabs.activityreport.articlesdownloader.DownloadPdfFiles;
import fr.inria.cominlabs.activityreport.core.DblpXmlParser;
import fr.inria.cominlabs.activityreport.model.Article;
import fr.inria.cominlabs.activityreport.model.Author;

@MultipartConfig
public class DemoServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private List<Article> articles ;
	private List<String> authorsLink ;
	private static final long serialVersionUID = 2251190694310845121L;
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		doPost(request, response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		articles = new ArrayList<Article>();
		authorsLink = new ArrayList<String>();
		response.setContentType("text/html;charset=UTF-8");
		
		
		int year = 0;
		PrintWriter out = response.getWriter();

		//out.println("Year: " + year + "<br/>");

		boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
		if (!isMultipartContent) {
			//out.println("You are not trying to upload<br/>");
			return;
		}
	//	out.println("You are trying to upload<br/>");
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			@SuppressWarnings("unchecked")
			List<FileItem> fields = upload.parseRequest(request);
		//	out.println("Number of fields: " + fields.size() + "<br/><br/>");
			Iterator<FileItem> it = fields.iterator();
			if (!it.hasNext()) {
				//out.println("No fields found");
				return;
			}
			//out.println("<table border=\"1\">");
			while (it.hasNext()) {
				FileItem fileItem = it.next();
				boolean isFormField = fileItem.isFormField();
				if (isFormField) {
					year=new Integer(fileItem.getString().trim()).intValue();
					//out.println("Year: " + year + "<br/>");
					//out.println("<td>regular form field</td><td>FIELD NAME: " + fileItem.getFieldName() + "<br/>STRING: " + fileItem.getString());
				} else {
					BufferedReader br = new BufferedReader(new InputStreamReader(fileItem.getInputStream()));
					String input=null;
					 while ((input = br.readLine()) != null) {
						 authorsLink.add(input);
					 }
				
				/*	out.println("<td>file form field</td><td>FIELD NAME: " + fileItem.getFieldName() +
							"<br/>STRING: " + fileItem.getString() +
							"<br/>NAME: " + fileItem.getName() +
							"<br/>CONTENT TYPE: " + fileItem.getContentType() +
							"<br/>SIZE (BYTES): " + fileItem.getSize() +
							"<br/>TO STRING: " + fileItem.toString()
							);
					out.println("</td>");*/
				}
				
			}
			
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		if(authorsLink.size()>0){
			System.out.println("AuhorLink is not null");
		for(String link :authorsLink ){
			try {
				List<Article> listarticle = DblpXmlParser.xmlFileParser(link.trim(),year);
				if(listarticle !=null){
				System.out.println("AuhorLink list article" + listarticle.size());
				articles.addAll(DownloadPdfFiles.getAllPdfFiles(listarticle));
				}
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
		
		display(out);
		}
		
	}
	
	public void display(PrintWriter out){
		
		
		if(articles.size()>0){
			out.println("<title> Report Generation Tool </title>");
			out.println("<center><h2>Report Generator</h2></center>");
			out.println("<table border=\"1\">");
			out.println("<tr>");
			out.println("<b><td>Author(s)</td><td>Title</td><td>Year</td><td>Summary</td></b>");
			out.println("</tr>");
			for(Article article : articles){
				out.println("<tr>");
				 String authors="";
				  
				  for(Author author : article.getAuthors()){
					  authors = authors + " " + author.getFirstLastName();
				  }
				  
				  out.println("<td>" + authors + "</td>");
				  out.println("<td>" + article.getTitle()+ "</td>");
				  out.println("<td>" + article.getYear() + "</td>");
				  String summary = "";
				  if(article.getSummary()!=null){
					  summary = article.getSummary();
				  }
				  out.println("<td>" + summary + "</td>");
				  out.println("</tr>");
			}
			out.println("</table>");
			out.close();
			
		}
		
		
	}

}
