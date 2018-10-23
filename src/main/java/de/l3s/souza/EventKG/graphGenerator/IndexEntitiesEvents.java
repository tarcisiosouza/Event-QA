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

public class IndexEntitiesEvents {

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
			
			index="souza_eventkg";
			type = "capture";
			bulkBuilderLength = 0;
			client = getTransportClient("master02.ib", 9350);
			// 0 walk ("/Volumes/Priest/Temporalia");
			bulkBuilder = client.prepareBulk();
			
	    	readFile ("/home/souza/EventKG/data/output/entities.nq");
	    	readFile ("/home/souza/EventKG/data/output/events.nq");	    	
	    	readFile ("/home/souza/EventKG/data/output/relations_base.nq");
			
	    	System.out.println("starting index process (total entries (events + entities: " + data.size() + ")");

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
		
		public static void readFile (String path) throws IOException
		{
			File fileEntities = new File (path);
			FileReader frEntities = new FileReader (fileEntities);
			BufferedReader br = new BufferedReader (frEntities);
			
			String lineEntities = "";
			System.out.println("reading file...: " + fileEntities.getName());
			while ((lineEntities=br.readLine())!=null)
	    	{
	    		
	    		if (lineEntities.contains("@prefix") || lineEntities.contains("@base") || lineEntities.isEmpty())
	    			continue;
	    		
	    		StringTokenizer tokenEntities = new StringTokenizer (lineEntities, "\n");
	    		while (tokenEntities.hasMoreElements())
	    		{
	    			StringTokenizer tokenId = new StringTokenizer (lineEntities);
	    			String id = tokenId.nextToken();
	    			
	    				if (data.containsKey(id))
	    				{
	    					String currentAtt = data.get(id);
	    					currentAtt = currentAtt + "\n" + lineEntities.replace(id + " ", "");
	    					data.put(id, currentAtt);
	    				}
	    				else
	    					data.put(id, lineEntities.replace(id + " ", ""));
	    				
	    				
	    			/*	if (lineEntities.contains("rdf:type") && i > 1)
	    				{
	    					data.remove(currentToken);
	    					
	    				}*/
	    				break;
	    			
	    		}
	    		
	    		//System.out.println("line " + i +" read" );
	    	}
			
			
		}
			
		
}
