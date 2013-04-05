package fr.inria.cominlabs.activityreport.haldb.main;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import fr.inria.cominlabs.activityreport.haldb.Documents;
import fr.inria.cominlabs.activityreport.haldb.Documents.Typdoc;
import fr.inria.cominlabs.activityreport.haldb.Documents.Typdoc.Document;
import fr.inria.cominlabs.activityreport.haldb.Documents.Typdoc.Document.Authors.Author;
import fr.inria.cominlabs.activityreport.haldb.ObjectFactory;
import fr.inria.cominlabs.activityreport.haldb.service.HaldbService;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
	// TODO Auto-generated method stub
	
	String filename = "halFoton.xml";
	try {
	    HaldbService.appendToDatabase(filename);
	} catch (JAXBException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	

    }
    
    public static void parsing (String filename){
	
	try {
	    JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
	     Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	    Documents documents = (Documents) unmarshaller.unmarshal(new File(filename));
	    List<Typdoc> listOfTypdoc = documents.getTypdoc();
	    int size = 0;
	    for(Typdoc typdoc : listOfTypdoc ){
		//System.out.println("typdocID: " + typdoc.getId() +  "typdocName:" + typdoc.getName());
		List<Document> listDocument = typdoc.getDocument();
		
		for(Document document : listDocument ){
		    size = size +1;
		    System.out.println("Titre: " + document.getTitle() +  " Abstract: " + document.getAbstract() +" AnneePub: " + document.getAnneepub() );
		    List<Author> listAuthor = document.getAuthors().getAuthor();
		    for(Author author : listAuthor ){
			System.out.println("SurName: "  + author.getSurname() + " ForeName:"  + author.getForename());
			   
		       }
		    
		    System.out.println("----------------------------------------");
		    
		}
		//System.out.println("Number of documents " + size);
	    }
	} catch (JAXBException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
    }

}
