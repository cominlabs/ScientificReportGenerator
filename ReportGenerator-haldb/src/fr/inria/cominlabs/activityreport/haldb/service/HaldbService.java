package fr.inria.cominlabs.activityreport.haldb.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import fr.inria.cominlabs.activityreport.haldb.Documents;
import fr.inria.cominlabs.activityreport.haldb.Documents.Typdoc;
import fr.inria.cominlabs.activityreport.haldb.Documents.Typdoc.Document;
import fr.inria.cominlabs.activityreport.haldb.Documents.Typdoc.Document.Authors.Author;
import fr.inria.cominlabs.activityreport.haldb.ObjectFactory;
import fr.inria.cominlabs.activityreport.haldb.model.Category;

public class HaldbService {
    
    private static final EntityManager entityManager = Persistence.createEntityManagerFactory(
	    "ReportGenerator-haldb").createEntityManager();
    private static final  EntityTransaction entityTransaction = entityManager.getTransaction();
    
    
   @SuppressWarnings("unchecked")
private static void createCategories(String filename) throws JAXBException{
       JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
	     Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	    Documents documents = (Documents) unmarshaller.unmarshal(new File(filename));
	    List<Typdoc> listOfTypdoc = documents.getTypdoc();
	    for(Typdoc typdoc : listOfTypdoc ){
		Query query = entityManager.createQuery("select category from Category category " + 
	                                                  "where category.categoryTitle = ?1");
		query.setParameter(1, typdoc.getName());
		List<Category> categories = query.getResultList();
		if(categories.size()==0){
		entityTransaction.begin();
		Category  category = new Category();
		category.setCategoryID(typdoc.getId());
		category.setCategoryTitle(typdoc.getName());
		entityManager.persist(category);
		entityTransaction.commit();
		}
	    }
	
    }
    
    @SuppressWarnings("unchecked")
    private static void createAuthors(String filename) throws JAXBException{
	JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
	     Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	    Documents documents = (Documents) unmarshaller.unmarshal(new File(filename));
	    List<Typdoc> listOfTypdoc = documents.getTypdoc();
	    for(Typdoc typdoc : listOfTypdoc ){
               List<Document> listDocument = typdoc.getDocument();
	       for(Document document : listDocument ){
		   List<Author> listAuthor = document.getAuthors().getAuthor();
		   for(Author author : listAuthor ){ 
		       Query query = entityManager.createQuery("select author from Author author " + 
                               "where author.authorSurName = ?1 and author.authorForeName = ?2");
		       query.setParameter(1, author.getSurname());
		       query.setParameter(2, author.getForename());
		       List< fr.inria.cominlabs.activityreport.haldb.model.Author> authors = query.getResultList();
		       if(authors.size()==0){
			   entityTransaction.begin();
			   fr.inria.cominlabs.activityreport.haldb.model.Author newAuthor = new fr.inria.cominlabs.activityreport.haldb.model.Author();
			   newAuthor.setAuthorSurName(author.getSurname());
			   newAuthor.setAuthorSurName(author.getForename());
			   entityManager.persist(newAuthor);
			   entityTransaction.commit(); 
		   } 
	       }
		
	    }
	}
    }
    
    
  @SuppressWarnings("deprecation")
private static void createDocuments(String filename) throws JAXBException{
      JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
	     Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	    Documents documents = (Documents) unmarshaller.unmarshal(new File(filename));
	    List<Typdoc> listOfTypdoc = documents.getTypdoc();
	    for(Typdoc typdoc : listOfTypdoc ){
        List<Document> listDocument = typdoc.getDocument();
	       for(Document document : listDocument ){
		   fr.inria.cominlabs.activityreport.haldb.model.Document storeDocument = new fr.inria.cominlabs.activityreport.haldb.model.Document();
		   List<Author> listAuthor = document.getAuthors().getAuthor();
		   List<fr.inria.cominlabs.activityreport.haldb.model.Author> storeListAuthor = new ArrayList<fr.inria.cominlabs.activityreport.haldb.model.Author>();
		   fr.inria.cominlabs.activityreport.haldb.model.Category storeCategory = new fr.inria.cominlabs.activityreport.haldb.model.Category();
           //construct the list of the authors of the document
		   for(Author author : listAuthor ){ 
		       fr.inria.cominlabs.activityreport.haldb.model.Author newAuthor = new fr.inria.cominlabs.activityreport.haldb.model.Author();
		       Query query1 = entityManager.createQuery("select author from Author author " + 
                               "where author.authorSurName = ?1 and author.authorForeName = ?2");
		       query1.setParameter(1, author.getSurname());
		       query1.setParameter(2, author.getForename());
		       fr.inria.cominlabs.activityreport.haldb.model.Author foundAuthor = (fr.inria.cominlabs.activityreport.haldb.model.Author) query1.getSingleResult();
		       newAuthor.setAuthorID(foundAuthor.getAuthorID());
		       newAuthor.setAuthorSurName(foundAuthor.getAuthorSurName());
		       newAuthor.setAuthorForeName(foundAuthor.getAuthorForeName());
		       storeListAuthor.add(newAuthor);
		   }
		   
		 //construct the list of the category of the document
		   Query query2 = entityManager.createQuery("select category from Category category " + 
			                                    "where author.CategoryID = ?1");
		   query2.setParameter(1, typdoc.getId());
		   fr.inria.cominlabs.activityreport.haldb.model.Category foundCategory = (fr.inria.cominlabs.activityreport.haldb.model.Category) query2.getSingleResult();
		   storeCategory.setCategoryID(foundCategory.getCategoryID());
		   storeCategory.setCategoryTitle(foundCategory.getCategoryTitle());
		   
		
		   //store the document
		   entityTransaction.begin();
		   storeDocument.setAuthors(storeListAuthor);
		   storeDocument.setCategory(storeCategory);
		   storeDocument.setDocumentTitle(document.getTitle());
		   storeDocument.setDocumentAbstract(document.getAbstract());
		   // retrieve the content if possible using GROBID otherwise store again the abstract in the place of the content
		   storeDocument.setDocumentContent(document.getAbstract());
		   storeDocument.setDatePub(new java.util.Date(document.getDatepub().getYear(),document.getDatepub().getMonth(),document.getDatepub().getDay()));
		   entityManager.persist(storeDocument);
		   entityTransaction.commit(); 
		   
		   
		
	    }
	}
      
	
    }
    
  
  public static void appendToDatabase(String filename) throws JAXBException{
      createCategories(filename);
      createAuthors(filename);
      createDocuments(filename);
      
	
  }
 
	    
	

}
