package de.l3s.souza.EventKG.graphGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

import de.l3s.souza.EventKG.queriesGenerator.Snapshot;
import de.l3s.souza.EventKG.queriesGenerator.relation.RelationSnapshot;
import de.l3s.souza.EventKG.queriesGenerator.relation.RelationUtils;


public class IndexRelations {

	private static String index;
	private static String type;
	private static String id;
	private static Map<String, RelationSnapshot> data = new HashMap<String, RelationSnapshot>();
	private static Map<String, String> events = new HashMap<String, String>();
	private static Map<String, String> entities = new HashMap<String, String>();
	private static Map<String, String> eventsMultipleRelations = new HashMap<String, String>();
	private static HashSet<String> dbos = new HashSet<String>();
	private static String currentEvent;
	private static StringBuilder sb = new StringBuilder();

	private static Map<String, String> dataToIndex = new HashMap<String, String>();
	
	public static StringBuilder getSb() {
		return sb;
	}
	
	public Map<String, String> getEntities() {
		return entities;
	}

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
		
		
		public Map<String,RelationSnapshot> getRelationRecords(BufferedReader br,String nodeType,int maxqueries) throws IOException
		{
			String line = "";
			String relationId = "";
			String predicateRelation = "";
			String relationsEvent = "";
			int currentRelation = 0;
			String currentRelationId = "";
			String nextRelationId ="";
			
			while ((line=br.readLine())!=null)
	    	{
	    		
				if (data.size() > maxqueries &&  !(nodeType.contains("entity")))
					return data;
				
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
				//	if (line.contains("<event_"))
					if (line.contains(nodeType))
	    			{
	    				StringTokenizer tokenLine = new StringTokenizer (line);
	    				tokenLine.nextToken();
	    				tokenLine.nextToken();
	    			 //  if (nodeType.contains("event"))
	    			   if (line.contains("<event_"))
	    			   {
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
	    			   else if (line.contains("<entity_"))
	    			   {
	    				   currentEvent = tokenLine.nextToken();
		    				
	    					if (entities.containsKey(currentEvent))
	    					{
	    						relationsEvent = entities.get(currentEvent);
	    						relationsEvent = relationsEvent + " " + relationId;
	    						entities.put(currentEvent, relationsEvent);
	    						
	    					}
	    					else
	    					{
	    						entities.put(currentEvent, relationId);
	    					}
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
					
					if (line.contains(nodeType))
	    			{
	    				StringTokenizer tokenLine = new StringTokenizer (line);
	    				tokenLine.nextToken();
	    				tokenLine.nextToken();
	    				currentEvent = tokenLine.nextToken();
	    				
	    				if (nodeType.contains("event"))
	    				{
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
	    				else
	    				{
	    					
	    					if (entities.containsKey(currentEvent))
	    					{
	    						relationsEvent = entities.get(currentEvent);
	    						relationsEvent = relationsEvent + " " + relationId;
	    						entities.put(currentEvent, relationsEvent);
	    						
	    					}
	    					else
	    					{
	    						entities.put(currentEvent, relationId);
	    					}
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
	    			
	    				relationId = tokenRelations.nextToken();
	    				predicateRelation = line.replaceAll(relationId + " ", "");
						updateMap (relationId, predicateRelation, "roletype");
		
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
			String currentIdRecord = "";
			while ((line=br.readLine())!=null)
	    	{
	    		
	    		if (line.contains("@prefix") || line.contains("@base") || line.isEmpty() )
	    			continue;
	    		
	    		StringTokenizer tokenRelations = new StringTokenizer (line);
	    		StringTokenizer tokenRelationId = new StringTokenizer (line);
	    		currentIdRecord = tokenRelationId.nextToken();
	    		
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
	    			
	    				relationId = tokenRelations.nextToken();
	    				predicateRelation = line.replaceAll(relationId + " ", "");
						updateMap (relationId, predicateRelation, "roletype");
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
		
		public Map<String,RelationSnapshot> getNewRelationIndex (String path,String nodeType,int maxqueries) throws IOException
		{
			Map<String,RelationSnapshot> output = new HashMap<String,RelationSnapshot>();
			//data.clear();
			File fileRelations = new File (path);
	    	FileReader frRelations = new FileReader (fileRelations);
	    	BufferedReader brRelations = new BufferedReader (frRelations);
	    	
	   /* 	if (path.contains("relations_entities_other"))
	    		output =  getRelationEntitiesRecords (brRelations);
	    	else*/
	    		output =  getRelationRecords (brRelations,nodeType,maxqueries);
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

