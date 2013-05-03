package fr.inria.cominlabs.activityreport.haldb.main;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import fr.inria.cominlabs.activityreport.haldb.model.Document;
import fr.inria.cominlabs.activityreport.marvin.core.mARC_connector;


public class MarvinMain {

    private static final EntityManager entityManager = Persistence.createEntityManagerFactory(
	    "ReportGenerator-haldb").createEntityManager();
   // private static final  EntityTransaction entityTransaction = entityManager.getTransaction();
    
    
    public static void main(String[] args) {
	
mARC_connector mymArc_connector = new  mARC_connector ("test","192.168.0.24",1254);
	 mymArc_connector.Connect();
	 
	 
	 /*
	 //this is the step to follow for creating a knowledge
	  mymArc_connector.KnowledgeCreate("ActivityReport", "null", "null", "null", "null");
	 
	 //save the knowledge after some operations (it is recommended to call this method sometimes)
	//  mymArc_connector.KnowledgeSave("ActivityReport");
	mymArc_connector.TableCreate("Publications","ActivityReport","null","null","master","DocID int64, DocTitle string, DocAbstract string, DocContent string, DocYear int32");
	 mymArc_connector.TableCreate("Authors","ActivityReport","null","null","simple","DocID int64, AuthorFirstName string, AuthorLastName string");
	  List<Document> listDocument = entityManager.createQuery("select document from Document document", Document.class).getResultList();
	 for(Document doc : listDocument){
	     String tableauChaine[] = {"DocID", doc.getDocumentID().toString(),"DocTitle",doc.getDocumentTitle(), "DocAbstract",doc.getDocumentAbstract(), "DocContent", doc.getDocumentContent(),"DocYear",doc.getYearPub().toString()};
	     mymArc_connector.TableInsert("Publications",tableauChaine);
	 }
	 
	 // this method is used to build the knowledge. The last options is "ref or "null". ref is to specify if marvin we'll do both knowledege and indexation
	 mymArc_connector.KnowledgeRebuild("ActivityReport", "DocContent DocAbstract DocTitle", 0, 2617,"ref");
	 mymArc_connector.KnowledgeSave("ActivityReport");
	 */
	  
	  //search example
	 // mymArc_connector.ContextsSetKnowledge("ActivityReport");
	 // mymArc_connector.KnowledgeAPI_ContextsGetBestResults("ActivityReport","machine-aided");
	  //mymArc_connector.KnowLedgeSimilarTitle("ActivityReport", "3d safe and intelligent trajectory generation for multi-axis machine tools using machine vision", "25", "10", "100");
	  //mymArc_connector.ResultsSetFormat("doctitle docabstract doccontent docyear act");
	  //mymArc_connector.ResultsSortBy("Act", false);
	  //mymArc_connector.ResultsFetch("20", "1");
	  //String[] resultDocTitle = mymArc_connector.GetMARCResult().GetDataByName("doctitle", -1);
	  //String[] resultDocContent = mymArc_connector.GetMARCResult().GetDataByName("doccontent", -1);
	  //String[] resultDocAbstract = mymArc_connector.GetMARCResult().GetDataByName("docabstract", -1);
	  //String[] resultDocYear = mymArc_connector.GetMARCResult().GetDataByName("docyear", -1);
	  //String[] resultDocAct = mymArc_connector.GetMARCResult().GetDataByName("Act", -1);
	 
	 /*
	   //String[] resultLines = mymArc_connector.GetMARCResult().GetDataByLine(row, idx)
	  int count = resultDocTitle.length;
	  for (int i =0;i < count;i++){
	 
	      System.out.println( i+ " ! " + resultDocTitle[i] + " ! " + resultDocAbstract[i] + " ! " + resultDocContent[i] + " ! " + resultDocYear[i]+" ! "+resultDocAct[i]);
	      
	  }
	  
	  */
	 mymArc_connector.KnowLedgeAPI_ContextsGetBestWords("ActivityReport", "machine ", "10", "10");
	 String[] shapes = mymArc_connector.GetMARCResult().GetDataByName("shape", 0);
	 String[] states = mymArc_connector.GetMARCResult().GetDataByName("state", 0);
	  for (int i =0;i < shapes.length;i++){
		 
	      System.out.println( i+ " ! "+ shapes[i]+ " ! "+ states[i]);
	      
	  }	 
	  
	  

    }

}
