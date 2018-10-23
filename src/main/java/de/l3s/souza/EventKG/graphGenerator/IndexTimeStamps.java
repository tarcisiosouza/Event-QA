package de.l3s.souza.EventKG.graphGenerator;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IndexTimeStamps {
	private static String index;
	private static String type;
	private static String id;
	private static Client client;
	private static Map<String, String> data = new HashMap<String, String>();
	private static Map<String, String> dataToIndex = new HashMap<String, String>();

	private static BulkRequestBuilder bulkBuilder;
	private static long bulkBuilderLength;
		public static void main (String args[]) throws IOException
		{
			
			
			index="souza_eventkg_timestamp";
			type = "capture";
			bulkBuilderLength = 0;
			client = getTransportClient("master02.ib", 9350);
			// 0 walk ("/Volumes/Priest/Temporalia");
			bulkBuilder = client.prepareBulk();
			
			File fileEntities = new File ("/home/souza/EventKG/data/output/relations_base.nq");
	    	FileReader frEntities = new FileReader (fileEntities);
	    	BufferedReader brEntities = new BufferedReader (frEntities);
	    	
	    	String lineEntities = "";
	    	String currentEntityID = "";
	    	String predicate = "";
	    	String dbpedia = "";
	    	String currentAttributes = "";
	    	int i = 0;
	    	
	    	System.out.println("reading relations_base file...");
	    	while ((lineEntities=brEntities.readLine())!=null)
	    	{
	    		
	    		if (lineEntities.contains("@prefix") || lineEntities.contains("@base") || lineEntities.isEmpty() || 
	    				!(lineEntities.contains("sem:hasEndTimeStamp") || lineEntities.contains("sem:hasBeginTimeStamp")))
	    			continue;
	    		i++;
	    		StringTokenizer tokenEntities = new StringTokenizer (lineEntities);
	    		while (tokenEntities.hasMoreElements())
	    		{
	    			String currentToken = tokenEntities.nextToken();
	    			
	    			/*if (currentToken.contains("wdt:"))
	    			{*/
	    				if (data.containsKey(currentToken))
	    				{
	    					String currentAtt = data.get(currentToken);
	    					currentAtt = currentAtt + "\n" + lineEntities.replace(currentToken, "");
	    					data.put(currentToken, currentAtt);
	    				}
	    				else
	    					data.put(currentToken, lineEntities.replace(currentToken, ""));
	    				
	    				
	    			/*	if (lineEntities.contains("rdf:type") && i > 1)
	    				{
	    					data.remove(currentToken);
	    					
	    				}*/
	    				break;
//	    			}
	    	
	    		}
	    		
	    		//System.out.println("line " + i +" read" );
	    	}
	    	System.out.println("starting index process (total properties: " + data.size() + ")");

	    	for (Entry<String, String> entry : data.entrySet())
			{
				dataToIndex.put("entityid", entry.getKey());
				dataToIndex.put("entityattributes", entry.getValue());
				id = entry.getKey();
				String json = new ObjectMapper().writeValueAsString(dataToIndex);						   
				bulkBuilder.add(client.prepareIndex(index, type, id).setSource(json));
				bulkBuilderLength++;
				dataToIndex.clear();
				if(bulkBuilderLength % 1000000 == 0){
				      System.out.println("##### " + bulkBuilderLength + " data indexed.");
				      BulkResponse bulkRes = bulkBuilder.execute().actionGet();
				      if(bulkRes.hasFailures()){
				    	  System.out.println("##### Bulk Request failure with "
				    	  		+ "error: " + bulkRes.buildFailureMessage());
				      }
				      bulkBuilder = client.prepareBulk();
				      
				   }
			}
	    	
	    	BulkResponse bulkRes = bulkBuilder.execute().actionGet();
		      if(bulkRes.hasFailures()){
		    	  System.out.println("##### Bulk Request failure with "
		    	  		+ "error: " + bulkRes.buildFailureMessage());
		      }
		      bulkBuilder = client.prepareBulk();
		      
		      
			data.clear();
			
			
			 
			
		//	 data.put (currentToken,lineEntities.replace(currentToken, ""));
	    	
	    }
		
		public static Client getTransportClient(String host, int port)
				throws UnknownHostException {

			Settings settings = Settings.settingsBuilder()
					.put("client.transport.sniff", true)
					// .put("shield.user", "souza:pri2006")
					.put("sniffOnConnectionFault", true)
					.put("cluster.name", "nextsearch").build();
			TransportClient client = TransportClient
					.builder()
					.settings(settings)
					.build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port))
			        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));

			return client;
		}
}
