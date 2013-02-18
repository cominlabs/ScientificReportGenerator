package fr.inria.cominlabs.activityreport.googlesearch;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inria.cominlabs.activityreport.googlesearch.GoogleSearch;

public class GoogleSearchTest {
	
	List<String> toSearch = new ArrayList<String>();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		toSearch.add("Decentralized polling with respectable participants");
		toSearch.add("Pulp: An adaptive gossip-based dissemination protocol for multi-source message streams");
		toSearch.add("Greedy Geographic Routing in Large-Scale Sensor Networks: A Minimum Network Decomposition Approach");
		toSearch.add("Geology: Modular Georecommendation in Gossip-Based Social Networks");
		toSearch.add("Diverging towards the common good: heterogeneous self-organisation in decentralised recommenders");
		toSearch.add("On the impact of users availability in OSNs");
		toSearch.add("Offline social networks: stepping away from the internet");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		for(String query : toSearch){
		try {
			assertFalse("Search sending nothing", GoogleSearch.googleSearchResults(query).getResponseData()==null);
			//System.out.println("Results ...." + GoogleSearch.googleSearchResults(query).getResponseData().getResults().toString() );
			//assertFalse("Search sending nothing for URL", GoogleSearch.googleSearchResults(query).getResponseData().getResults().get(0).getUrl()==null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		}
	}

}
