package de.l3s.souza.EventKG.graphGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

import de.l3s.souza.EventKG.queriesGenerator.Snapshot;
import de.l3s.elasticquery.RelationSnapshot;
import de.l3s.souza.EventKG.queriesGenerator.RelationUtils;

public class IndexRelations {

	private static String index;
	private static String type;
	private static String id;
	private static Client client;
	private static Map<String, RelationSnapshot> data = new HashMap<String, RelationSnapshot>();
	private static Map<String, String> events = new HashMap<String, String>();
	private static Map<String, String> eventsMultipleRelations = new HashMap<String, String>();
	private static HashSet<String> dbos = new HashSet<String>();
	private static String currentEvent;
	private static Map<String, String> dataToIndex = new HashMap<String, String>();

	private static BulkRequestBuilder bulkBuilder;
	private static long bulkBuilderLength;
	
	
	
	public static HashSet<String> getDbos() {
		return dbos;
	}
	
	public Map<String, String> getEvents() {
		return eventsMultipleRelations;
	}

		public IndexRelations() {
		
			currentEvent = "";
			data = new HashMap<String, RelationSnapshot>();
	}

		
		public static void main (String args[]) throws IOException
		{
			
			index="souza_eventkg_relations_all";
			type = "capture";
			bulkBuilderLength = 0;
			client = getTransportClient("master02.ib", 9350);
			
			bulkBuilder = client.prepareBulk();
			
			/*System.out.println("reading relations_other file...");
			readNewFile("/home/souza/EventKG/data/output/relations_other.nq");*/
			System.out.println("reading relations_entities_other file...");
			readNewFile("/home/souza/EventKG/data/output/relations_entities_other.nq");
			/*System.out.println("reading relations_entities_temporal file...");
			readNewFile("/home/souza/EventKG/data/output/relations_entities_temporal.nq");*/

	    	System.out.println("starting index process (total relations : " + data.size() + ")");

	    	for (Entry<String, RelationSnapshot> entry : data.entrySet())
			{
	    	
				dataToIndex.put("relationid", entry.getKey());
				dataToIndex.put("type", ( entry.getValue().getType().isEmpty() ? "":  entry.getValue().getType()));
				dataToIndex.put("subject", ( entry.getValue().getSubject().isEmpty() ? "":  entry.getValue().getSubject()));
				dataToIndex.put("object", ( entry.getValue().getObject().isEmpty() ? "":  entry.getValue().getObject()));
				dataToIndex.put("begintimestamp", ( entry.getValue().getBeginTimestamp().isEmpty() ? "":  entry.getValue().getBeginTimestamp()));
				dataToIndex.put("endtimestamp", ( entry.getValue().getEndTimestamp().isEmpty() ? "":  entry.getValue().getEndTimestamp()));
				dataToIndex.put("roletype", ( entry.getValue().getRoleType().isEmpty() ? "":  entry.getValue().getRoleType()));

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
		
		
		private static void readRecords(BufferedReader br) throws IOException
		{
			String line = "";
			String relationId = "";
			String predicateRelation = "";
			Snapshot relation;
			currentEvent = "";
			while ((line=br.readLine())!=null)
	    	{
	    		
	    		if (line.contains("@prefix") || line.contains("@base") || line.isEmpty() )
	    			continue;
	    		
	    		StringTokenizer tokenRelations = new StringTokenizer (line);
	    		
	    		if (line.contains("rdf:type"))
				{
					relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "type");
				
				}
	    		
	    		if (line.contains("rdf:subject"))
				{
	    			
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					predicateRelation = tokenRelations.nextToken();
					predicateRelation = tokenRelations.nextToken();
					updateMap (relationId, predicateRelation, "subject");

				}
	    		
	    		if (line.contains("rdf:object"))
				{
	    			
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					predicateRelation = tokenRelations.nextToken();
					predicateRelation = tokenRelations.nextToken();
					updateMap (relationId, predicateRelation, "object");


				}
	    		
	    		if (line.contains("sem:hasEndTimeStamp"))
				{
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "endtimestamp");

				}
	    		
	    		if (line.contains("sem:hasBeginTimeStamp"))
				{
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "begintimestamp");

				}
	    		
	    		
	    		if (line.contains("sem:roleType"))
				{
	    			/*if (line.contains("dbo:"))
	    			{*/
	    				relationId = tokenRelations.nextToken();
	    				predicateRelation = line.replaceAll(relationId + " ", "");
						updateMap (relationId, predicateRelation, "roletype");
		
	    		/*	}
	    			else
	    				data.remove(relationId);
			*/
				}
	    	}
			
		}
		
		
		public Map<String,RelationSnapshot> getRelationRecords(BufferedReader br) throws IOException
		{
			String line = "";
			String relationId = "";
			String predicateRelation = "";
			String relationsEvent = "";

			Snapshot relation;
			
			while ((line=br.readLine())!=null)
	    	{
	    		
	    		if (line.contains("@prefix") || line.contains("@base") || line.isEmpty() )
	    			continue;
	    		
	    		StringTokenizer tokenRelations = new StringTokenizer (line);
	    		
	    		if (line.contains("rdf:type"))
				{
					relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "type");
				
				}
	    		
	    		if (line.contains("rdf:subject"))
				{
	    		
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "subject");
					if (line.contains("<event_"))
	    			{
	    				StringTokenizer tokenLine = new StringTokenizer (line);
	    				tokenLine.nextToken();
	    				tokenLine.nextToken();
	    				currentEvent = tokenLine.nextToken();
	    				
	    					if (events.containsKey(currentEvent))
	    					{
	    						relationsEvent = events.get(currentEvent);
	    						relationsEvent = relationsEvent + " " + relationId;
	    						events.put(currentEvent, relationsEvent);
	    						eventsMultipleRelations.put(currentEvent, relationsEvent);
	    					}
	    					else
	    					{
	    						events.put(currentEvent, relationId);
	    					}
	    			 }

				}
	    		
	    		if (line.contains("owl:sameAs"))
				{
	    			
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "owl");


				}
	    		
	    		if (line.contains("rdfs:label"))
				{
	    			
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "label");


				}
	    		
	    		if (line.contains("rdf:object"))
				{   	
	    			
	    			
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "object");
					
					if (line.contains("<event_"))
	    			{
	    				StringTokenizer tokenLine = new StringTokenizer (line);
	    				tokenLine.nextToken();
	    				tokenLine.nextToken();
	    				currentEvent = tokenLine.nextToken();
	    				
	    					if (events.containsKey(currentEvent))
	    					{
	    						relationsEvent = events.get(currentEvent);
	    						relationsEvent = relationsEvent + " " + relationId;
	    						events.put(currentEvent, relationsEvent);
	    						eventsMultipleRelations.put(currentEvent, relationsEvent);
	    					}
	    					else
	    					{
	    						events.put(currentEvent, relationId);
	    					}
	    			 }

				}
	    		
	    		if (line.contains("sem:hasEndTimeStamp"))
				{
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "endtimestamp");

				}
	    		
	    		if (line.contains("sem:hasBeginTimeStamp"))
				{
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "begintimestamp");

				}
	    		
	    		
	    		if (line.contains("sem:roleType"))
				{
	    			/*if (line.contains("dbo:"))
	    			{*/
	    				relationId = tokenRelations.nextToken();
	    				predicateRelation = line.replaceAll(relationId + " ", "");
						updateMap (relationId, predicateRelation, "roletype");
		
	    		/*	}
	    			else
	    				data.remove(relationId);
			*/
				}
	    	}
			
			return data;
			
		}
		
		//index for relations_entities_other.nq file where relationId is the entityId
		public Map<String,RelationSnapshot> getRelationEntitiesRecords(BufferedReader br) throws IOException
		{
			String line = "";
			String relationId = "";
			String predicateRelation = "";
			Snapshot relation;
			String currentRecord = "";
			String idRecord = "";
			while ((line=br.readLine())!=null)
	    	{
	    		
	    		if (line.contains("@prefix") || line.contains("@base") || line.isEmpty() )
	    			continue;
	    		
	    		StringTokenizer tokenRelations = new StringTokenizer (line);
	    		StringTokenizer tokenRelationId = new StringTokenizer (line);
	    	
	    		if (line.contains("rdf:type"))
				{
					relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "type");
					currentRecord = currentRecord + line;
				}
	    		
	    		if (line.contains("rdf:subject"))
				{
	    			
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "subject");
					currentRecord = currentRecord + line;

				}
	    		
	    		if (line.contains("owl:sameAs"))
				{
	    			
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "owl");
					currentRecord = currentRecord + line;

				}
	    		
	    		if (line.contains("rdfs:label"))
				{
	    			
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "label");


				}
	    		
	    		if (line.contains("rdf:object"))
				{   			
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "object");
					currentRecord = currentRecord + line;

				}
	    		
	    		if (line.contains("sem:hasEndTimeStamp"))
				{
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "endtimestamp");

				}
	    		
	    		if (line.contains("sem:hasBeginTimeStamp"))
				{
	    			relationId = tokenRelations.nextToken();
					predicateRelation = line.replaceAll(relationId + " ", "");
					updateMap (relationId, predicateRelation, "begintimestamp");
					currentRecord = currentRecord + line;
				}
	    		
	    		
	    		if (line.contains("sem:roleType"))
				{
	    			if (line.contains("dbo:"))
	    			{
	    				relationId = tokenRelations.nextToken();
	    				predicateRelation = line.replaceAll(relationId + " ", "");
						updateMap (relationId, predicateRelation, "roletype");
						currentRecord = currentRecord + line;
	    			}
	    			else
	    				data.remove(relationId);
			
				}
	    	}
			
			return data;
			
		}
		
		
		private static void readNewFile (String path) throws IOException
		{
			File fileRelations = new File (path);
	    	FileReader frRelations = new FileReader (fileRelations);
	    	BufferedReader brRelations = new BufferedReader (frRelations);
	    	readRecords (brRelations);
	    	
		}
		
		public Map<String,RelationSnapshot> getNewRelationIndex (String path) throws IOException
		{
			Map<String,RelationSnapshot> output = new HashMap<String,RelationSnapshot>();
			data.clear();
			File fileRelations = new File (path);
	    	FileReader frRelations = new FileReader (fileRelations);
	    	BufferedReader brRelations = new BufferedReader (frRelations);
	    	
	    	if (path.contains("relations_entities_other"))
	    		output =  getRelationEntitiesRecords (brRelations);
	    	else
	    		output =  getRelationRecords (brRelations);
	    	brRelations.close();
	    	return output;
	    	
		}
	
		private static void updateMap (String relationId,String predicateRelation, String property)
		{
			RelationSnapshot relation;
			RelationUtils relationUtils = new RelationUtils ();
			if (data.containsKey(relationId))
			{
				relation = new RelationSnapshot ();
				relation = data.get(relationId);
				relation = relationUtils.updatePredicateRelation(property, predicateRelation, relation);
				
				if (!relation.getRoleType().isEmpty() && (relation.getRoleType().contains("dbo")))
				{
					dbos.add(relation.getRoleType());
					data.put(relationId, relation);
				}
				else
					data.put(relationId, relation);
				
			}
			else
			{
				relation = new RelationSnapshot ();
				relation = relationUtils.updatePredicateRelation(property, predicateRelation, relation);
				
				if (!relation.getRoleType().isEmpty() && (relation.getRoleType().contains("dbo")))
				{
					dbos.add(relation.getRoleType());
					data.put(relationId, relation);
				}
				else
					data.put(relationId, relation);
						
			}								
			
		}
	
}

