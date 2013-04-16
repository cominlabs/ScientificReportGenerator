package fr.inria.cominlabs.activityreport.haldb.service;

import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import fr.inria.cominlabs.activityreport.articlesdownloader.Downloader;
import fr.inria.cominlabs.activityreport.haldb.Documents;
import fr.inria.cominlabs.activityreport.haldb.Documents.Typdoc;
import fr.inria.cominlabs.activityreport.haldb.Documents.Typdoc.Document;
import fr.inria.cominlabs.activityreport.haldb.Documents.Typdoc.Document.Authors.Author;
import fr.inria.cominlabs.activityreport.haldb.ObjectFactory;
import fr.inria.cominlabs.activityreport.haldb.model.Category;
import fr.inria.cominlabs.activityreport.services.ReportGenerator;

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
		System.out.println("typdocID: " + typdoc.getId() +  "   typdocName:" + typdoc.getName());
		Query query = entityManager.createQuery("select category from Category category " + 
	                                                  "where category.categoryID = ?1");
		query.setParameter(1, typdoc.getId());
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
			   newAuthor.setAuthorForeName(author.getForename());
			   entityManager.persist(newAuthor);
			   entityTransaction.commit(); 
		   } 
	       }
		
	    }
	}
    }
    
    
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
		   fr.inria.cominlabs.activityreport.haldb.model.Category storeCategory = new fr.inria.cominlabs.activityreport.haldb.model.Category();
           //construct the list of the authors of the document
		   for(Author author : listAuthor ){ 
		      // fr.inria.cominlabs.activityreport.haldb.model.Author newAuthor = new fr.inria.cominlabs.activityreport.haldb.model.Author();
		       Query query1 = entityManager.createQuery("select author from Author author " + 
                               "where author.authorSurName = ?1 and author.authorForeName = ?2");
		       query1.setParameter(1, author.getSurname());
		       query1.setParameter(2, author.getForename());
		       fr.inria.cominlabs.activityreport.haldb.model.Author foundAuthor = (fr.inria.cominlabs.activityreport.haldb.model.Author) query1.getSingleResult();
		       System.out.println("Comparaison + " + "docAuth:" +  author.getSurname() + "DataBaseAuth: " + foundAuthor.getAuthorSurName());
		       System.out.println("Comparaison + " + "docAuth :" +  author.getForename() + "DataBaseAuth: " + foundAuthor.getAuthorForeName());
		    
		 //construct the list of the category of the document
		   Query query2 = entityManager.createQuery("select category from Category category " + 
			                                    "where category.categoryID = ?1");
		   query2.setParameter(1, typdoc.getId());
		   fr.inria.cominlabs.activityreport.haldb.model.Category foundCategory = (fr.inria.cominlabs.activityreport.haldb.model.Category) query2.getSingleResult();
		   storeCategory.setCategoryID(foundCategory.getCategoryID());
		   storeCategory.setCategoryTitle(foundCategory.getCategoryTitle());
		   
		
		   //store the document
		   entityTransaction.begin();
		 
		   storeDocument.setCategory(storeCategory);
		   storeDocument.setDocumentTitle(document.getTitle());
		   storeDocument.setDocumentAbstract(document.getAbstract());
		   
		   // retrieve the content if possible using GROBID otherwise store again the abstract in the place of the content
		   Vector<String> avoidUrls = new Vector<String>();
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00721069/PDF/bramerie_JSTQE_2010_review_HAL.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/docs/00/72/10/69/PDF/bramerie_JSTQE_2010_review_HAL.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00632970/PDF/manuscript.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00627233/PDF/PrimCountChebyVersion2.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00650320/PDF/ChebyRH.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00741515/PDF/cmame12_td.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00638617/PDF/TSAPSVlas_corr.pdf");
		   avoidUrls.add("http://hal.inria.fr/hal-00752688/PDF/sosym-gra2mol.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00735686/PDF/IJDEM_paper_Ruxandra_TAPU_Titus_ZAHARIA_with_figures.pdf");
		   avoidUrls.add("http://hal-institut-mines-telecom.archives-ouvertes.fr/hal-00471634/PDF/bej367.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00712310/PDF/ElasticVectorSpaceFinalSub.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00525994/PDF/full_paper4AS.pdf");
		   avoidUrls.add("http://hal-institut-mines-telecom.archives-ouvertes.fr/hal-00696181/PDF/Moussallam2012_RSSMP.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00724636/PDF/2012-Journal-IJISR-Boudguiga.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00776154/PDF/i3espNewOptKurt.pdf");
		   avoidUrls.add("http://hal-ensmp.archives-ouvertes.fr/hal-00660343/PDF/Stabilization_half_spin.pdf");
		   avoidUrls.add("http://hal-ensmp.archives-ouvertes.fr/hal-00722027/PDF/NaB_ActaMat2012.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00505167/PDF/AAP773-HAL.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00757737/PDF/LL-JNR-14-Orig.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00722640/PDF/JPV-Fuel_Cells-Orig.pdf");
		   avoidUrls.add("http://hal-ensmp.archives-ouvertes.fr/hal-00576882/PDF/BasicJMMR2010IEEE_TPE.pdf");
		   avoidUrls.add("http://hal.archives-ouvertes.fr/hal-00589738/PDF/DerivedBasedSVM-r1-soumis.pdf");
		   
		   if((document.getFilesUrl()!=null) && (typdoc.getId().startsWith("ART_")) && (!avoidUrls.contains(document.getFilesUrl().getFileUrl()))){
		       System.out.println("This is the title " + document.getTitle());
		       System.out.println("URL where to download " + document.getFilesUrl().getFileUrl());  
		       boolean val = Downloader.downloadFileFromUrl(document.getFilesUrl().getFileUrl(),document.getTitle());
				
		    if(val){
			String teststring ="";
		       File file = new File (document.getTitle());
		       String content = ReportGenerator.processFullDocument(file);
		       if(teststring.equals(content)){
			   storeDocument.setDocumentContent(document.getAbstract());
		       }
		       else
		       {
			   storeDocument.setDocumentContent(content);
		       }
			      
		       file.delete(); 
		       }
		    else{
			storeDocument.setDocumentContent(document.getAbstract());
		    }
			   
		   }
		   
		   else {
		       storeDocument.setDocumentContent(document.getAbstract());
		       
		   }
		  
		   
		   int year=0;
		   if (document.getDatepub() != null){ 
		       year = document.getDatepub().getYear();
		   }
		   else if (document.getAnneepub()>0){ 
		       year = document.getAnneepub();
		       
		   }
		   storeDocument.setYearPub(year);
		   entityManager.persist(storeDocument);
		   entityTransaction.commit(); 
		   if(foundAuthor!=null){ 
		       entityTransaction.begin();
			 fr.inria.cominlabs.activityreport.haldb.model.Author locateAuthor = entityManager.find(fr.inria.cominlabs.activityreport.haldb.model.Author.class, foundAuthor.getAuthorID());
			 locateAuthor.addDocuments(storeDocument);
			 entityManager.merge(locateAuthor);
			 //entityManager.flush();
			 entityTransaction.commit();
				       }
				       
				   }
		   
	    }
	}
      
	
    }
    
  
  public static void appendToDatabase(String filename) throws JAXBException{
      createCategories(filename);
      createAuthors(filename);
      createDocuments(filename);
      
	
  }
 
	    
	

}
