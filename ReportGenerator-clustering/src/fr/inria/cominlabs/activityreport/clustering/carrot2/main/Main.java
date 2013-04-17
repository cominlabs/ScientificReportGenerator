package fr.inria.cominlabs.activityreport.clustering.carrot2.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.source.etools.EToolsDocumentSource;
import org.carrot2.source.microsoft.Bing3WebDocumentSource;
import org.carrot2.source.microsoft.Bing3WebDocumentSourceDescriptor;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
	
	{
	/* The role of Controller object is to manage the processing pipeline     */
	final Controller controller = ControllerFactory.createSimple();
	
	
	/* perform processing   */
	
	final ProcessingResult result = controller.process("data maning",100, EToolsDocumentSource.class,LingoClusteringAlgorithm.class);
	displayResults(result);
	System.out.println("-------------------------------Another One------------------------------------------------");
	 }
	
	{
		/* The role of Controller object is to manage the processing pipeline     */
		final Controller controller = ControllerFactory.createSimple();
		
		
		/* My bing API key    */
		String mykey = "1uRcMh534LjgiFekLqp4Evu0iLFzf39X9cfYfpiWKuo=" ;
		
		/* Prepare attributes     */
		
		final Map<String, Object> attributes = new HashMap<String, Object>();
		Bing3WebDocumentSourceDescriptor.attributeBuilder(attributes).appid(mykey);
		
		/* Query and the required number of results */
		attributes.put(CommonAttributesDescriptor.Keys.QUERY, "top-k query");
		attributes.put(CommonAttributesDescriptor.Keys.RESULTS, 50);
		
		
		/* perform processing   */
		
		final ProcessingResult result = controller.process(attributes, Bing3WebDocumentSource.class,STCClusteringAlgorithm.class);
		displayResults(result);
	}
		
	
	
    }
    
    public static void displayResults(ProcessingResult result){
	final List<Cluster> clusters = result.getClusters();
	for (Cluster cluster : clusters){
	    System.out.println("Cluster: " + cluster.getLabel());
	    for(Document document : cluster.getDocuments()){
		System.out.println("\t" + "Document: " + document.getTitle());
	    }
	}
	
    }

}
