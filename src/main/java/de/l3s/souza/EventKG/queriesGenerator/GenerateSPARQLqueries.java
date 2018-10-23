package de.l3s.souza.EventKG.queriesGenerator;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.l3s.elasticquery.ElasticMain;
import de.l3s.elasticquery.RelationSnapshot;
import de.l3s.souza.EventKG.graphGenerator.IndexRelations;

public class GenerateSPARQLqueries {

	private static Map<String,RelationSnapshot> RelationsOther = new HashMap<String,RelationSnapshot>(); 
	private static RelationSnapshot relationSnapshot = new RelationSnapshot ();
	private static Map<String,RelationSnapshot> RelationsEntitiesTemporal = new HashMap<String,RelationSnapshot>(); 
	private static Map<String,RelationSnapshot> RelationsEntitiesOther = new HashMap<String,RelationSnapshot>(); 
	private static HashSet<String> pairRelationsRoletypeEvent = new HashSet<String>(); 
	private static HashSet<String> pairRelationsRoletypeEntity ;

	private static String relationFile = "";
	private static HashSet<String> roleTypes = new HashSet<String>();
	private static String relationId = "";
	private static String predicateRelation = "";
	private static BufferedWriter out ;
	private static int MAX_KEYS = 70;
	private static HashSet<Integer> generatedKeys = new HashSet<Integer>();
	private static HashSet<String> generatedRelationKeys = new HashSet<String>();
	private static RelationUtils relationUtils;
	private static ElasticMain es ;
	private static String query = "";
	private static ElasticMain es2;
	private static int MAX_TRIPLES = 6000;
	private static int totalQueriesGenerated = 0;
	private static StringBuilder sb ;
	private static int generated = 0;
	private static Map<String,String> entitiesEvents = new HashMap<String,String>();
	private static Map<String,String> events = new HashMap<String,String>();
	private static Map<String,String> eventsFromRelationsOther = new HashMap<String,String>();
	private static RandomFilter randomFilter;

	private static ArrayList<String> relationsKeySet;
	private static ArrayList<String> relationsKeySetEntities;

	private static IndexRelations indexRelations;
	private static HashMap <String, RelationSnapshot> generatedRelations = new HashMap<String,RelationSnapshot> ();
	private static Map<String,RelationSnapshot> RelationsIndex = new HashMap<String,RelationSnapshot>();
	private static Map<String,RelationSnapshot> RelationsEntitiesOtherIndex = new HashMap<String,RelationSnapshot>();

	private static Map<String,RelationSnapshot> EntitiesIndex = new HashMap<String,RelationSnapshot>();
	private static Map<String,RelationSnapshot> EventsIndex = new HashMap<String,RelationSnapshot>();
	private static Map<String,String> wikidataProp = new HashMap<String,String>();
	private static TimeStampUtils timeStampUtils;
	private static PropertyUtils propertyUtils;
	private static RelationSnapshot r1;
	private static RelationSnapshot r2;
	private static String objectIdr1 = "";
	private static String objectIdr2 = "";
	private static String subjectId = "";
	private static String subjectIdr1 = "";
	private static String subjectIdr2 = "";
	private static HashSet<String> dbos = new HashSet<String>();
	public static void main (String args[]) throws IOException, InterruptedException
	{
		
		indexRelations = new IndexRelations ();
		randomFilter = new RandomFilter ();
		r1 = new RelationSnapshot ();
		r2 = new RelationSnapshot ();
		pairRelationsRoletypeEntity = new HashSet<String>();
		RelationsIndex = indexRelations.getNewRelationIndex("/home/souza/EventKG/data/output/relations_other.nq");
		eventsFromRelationsOther = indexRelations.getEvents();
		dbos = indexRelations.getDbos();
		//RelationsEntitiesOtherIndex = indexRelations.getNewRelationIndex("/home/souza/EventKG/data/output/relations_entities_other.nq");
		removeNonDboRelations ();
		//EntitiesIndex = indexRelations.getNewRelationIndex("/home/souza/EventKG/data/output/entities.nq");
		//EventsIndex = indexRelations.getNewRelationIndex("/home/souza/EventKG/data/output/events.nq");
		File fileWd = new File ("/home/souza/EventKG/data/output/property_labels.nq");
		FileReader fileWdR = new FileReader (fileWd);
		BufferedReader brFileWdR = new BufferedReader (fileWdR);
		
		String lineBrFileWdR= "";
		
		while ((lineBrFileWdR = brFileWdR.readLine())!=null)
		{
			if (lineBrFileWdR.contains("@prefix") || lineBrFileWdR.contains("@base") || lineBrFileWdR.isEmpty() )
    			continue;
			
			String wdId = "";
			String wdValue = "";
			StringTokenizer tokenWdt = new StringTokenizer (lineBrFileWdR);
    		
    		if (lineBrFileWdR.contains("wdt"))
			{
				wdId = tokenWdt.nextToken();
				wdValue = lineBrFileWdR.replaceAll(wdId + " ", "" ) + "\n";
				if (wikidataProp.containsKey(wdId))
				{
					String currentWdValue = wikidataProp.get(wdId);
					currentWdValue = currentWdValue + wdValue;
					wikidataProp.put(wdId, currentWdValue);
				}
				else
					wikidataProp.put(wdId, wdValue);

			
			}
		}
		
		relationsKeySet = new ArrayList<>(RelationsIndex.keySet());
		relationsKeySetEntities = new ArrayList<>(eventsFromRelationsOther.keySet());
		sb = new StringBuilder ();
		out = new BufferedWriter
			    (new OutputStreamWriter(new FileOutputStream("queries.nq"),"UTF-8"));
		
		es = new ElasticMain("souza_eventkg", 1);
    	es2 = new ElasticMain("", 1, "entityid","souza_eventkg");
    	es.setIndexName("souza_eventkg");
		
    	propertyUtils = new PropertyUtils (es, wikidataProp);
    	timeStampUtils = new TimeStampUtils (propertyUtils);
    	relationUtils = new RelationUtils (propertyUtils);
    	
    	generateQueries ("relation");
		 System.out.print("keys generated! Building queries...");
		 
		 out.write (sb.toString());
		 out.close();

		 System.out.print("Finished!");

	
	}
	
	private static void removeNonDboRelations ()
	{
		RelationSnapshot relation;
		String relationIdsDbo;
	
		Map<String,String> eventsFromRelationsOtherDbo = new HashMap<String,String>();
		for (Entry<String,String> entry : eventsFromRelationsOther.entrySet())
		{
			String relations = entry.getValue();
		
			relationIdsDbo = "";
			StringTokenizer token = new StringTokenizer (relations);
			
			while (token.hasMoreTokens())
			{
				try {
				String currentRelationId = token.nextToken();
				relation = RelationsIndex.get(currentRelationId);
				
				if (relation.getRoleType().contains("dbo"))
				{
					relationIdsDbo = relationIdsDbo + " " + currentRelationId;
				}
					
				} catch (Exception e)
				{
					continue;
				}
			}
			
			if (!relationIdsDbo.isEmpty())
				eventsFromRelationsOtherDbo.put(entry.getKey(), relationIdsDbo);
		}
		
		eventsFromRelationsOther = eventsFromRelationsOtherDbo;
	}
	private static void generateQueries (String type) throws IOException, InterruptedException
	{
		
		 int totalGenerated = 0;
		 int maxint = 0;
		 int randomNumberGenerated = 0;
		 ArrayList<String> nodeTypes = new ArrayList<String> ();
		 String node;
		 nodeTypes.add("entity");
		 nodeTypes.add("event");
		
		 Random random = new Random (System.currentTimeMillis());
		 System.out.print("Generating keys...");

		 for (Entry<String,String> entry : entitiesEvents.entrySet())
		 {
			 if (entry.getKey().contains("<event"))
				 events.put(entry.getKey(), entry.getValue());
		 }
		 
		// while (totalGenerated <= MAX_KEYS)
		 while (!(RelationsIndex.isEmpty()) && !(dbos.isEmpty()) && generated  < 4600)
		 {
		
			 node = randomFilter.getRandomValue(nodeTypes);
			
			 if (eventsFromRelationsOther.isEmpty())
			 	node = "entity";
			 
			 if (node.contains("entity"))
				 maxint = RelationsIndex.size() ;
			 else
				 maxint = eventsFromRelationsOther.size();
		//	while (generatedKeys.contains(randomNumberGenerated = random.nextInt(maxint)))
				
			
			 randomNumberGenerated = random.nextInt(maxint);
			
			generatedKeys.add(randomNumberGenerated);
			
	   /* 	if (type.contains("event"))
	    	{
	    		relationUtils.initializeRelation(relationSnapshot);
	    		if ((!relationSnapshot.getId().contains("<event") || (relationSnapshot.hasPlace().isEmpty())))
	    			continue;
	    	}
	    */	
	    	String objectId = "";
    		
    		String eventAttrib = "";
    		
	    	if (node.contains("entity"))
	    	{
	    		String relationId = relationsKeySet.get(randomNumberGenerated);
	    		relationSnapshot = RelationsIndex.get(relationId);
	    		
	    		RelationsIndex.remove(relationId);
	    		relationsKeySet.remove(randomNumberGenerated);
	    		if (!relationSnapshot.getRoleType().contains("dbo:"))
	    			continue;
	    	
	    		dbos.remove(relationSnapshot.getRoleType());
	    	/*	if (relationSnapshot.getRoleType().contains("wdt:"))
	    			continue;
	    		*/
	    		/*if (dbos.contains(relationSnapshot.getRoleType()))
	    			continue;
	    		*/
	    		//dbos.add(relationSnapshot.getRoleType());
	    		
	    		subjectId = relationUtils.getSubObjIdFromRelation(relationSnapshot, "subject");
	    		objectId = relationUtils.getSubObjIdFromRelation(relationSnapshot, "object"); 
	    		
	    		String objectLabel = propertyUtils.getLabelFirstEnglishEntity (objectId);
	    		String subjectLabel = propertyUtils.getLabelFirstEnglishEntity (subjectId);
	    		
	    		String roleType = relationUtils.getRoleType (relationSnapshot);
	    		
	    		if (relationSnapshot.getObject().contains("<event"))
	    			eventAttrib = propertyUtils.getAttributes (objectId);
	    		
	    		if (relationSnapshot.getSubject().contains("<event"))
	    			eventAttrib = propertyUtils.getAttributes (subjectId);
	    		
	    		if (eventAttrib.isEmpty())
	    			eventAttrib = propertyUtils.getAttributes (subjectId);
	    	/*	
	    		System.out.println("Object: " + objectLabel);
	    		System.out.println("Subject: " + subjectLabel);
	    		System.out.println("roletype: " + (roleType.contains("wdt") ? roleType + ": " + getLabelFirstEnglishProperty(roleType): roleType));
	    		System.out.println("Event Attrib: " + eventAttrib);
	    		System.out.println("------------------------------");
	    		//initializeRelation();
	    	/*	if (!(relationSnapshot.getObject()).contains("event"))
	    			continue;*/
	    	}
	    	
	    	if (node.contains("event"))
	    	{
	    		String eventId = relationsKeySetEntities.get(randomNumberGenerated);
	    		String relations = eventsFromRelationsOther.get(eventId);
	    		StringTokenizer tokenRelations = new StringTokenizer (relations);
	    		try {
	    			
	    			String relationId1 = tokenRelations.nextToken();
	    			String relationId2 = tokenRelations.nextToken();
	    		
	    			r1 = RelationsIndex.get(relationId1);
	    			r2 = RelationsIndex.get(relationId2);
	    			r1.setId(relationId1);
	    			r2.setId(relationId2);
	    		} catch (Exception e)
	    		{
	    			//not enough relations left to construct a query, then delete the event node
	    			eventsFromRelationsOther.remove(eventId);
		    		relationsKeySetEntities.remove(randomNumberGenerated);
	    			continue;
	    		}
	    	
	    		if (pairRelationsRoletypeEvent.contains(r1.getRoleType() + " " + r2.getRoleType()))
	    		{
	    			relations = relations.replaceAll(r1.getRoleType() + " " + r2.getRoleType(), "");
	    			eventsFromRelationsOther.put(eventId, relations);
	    			continue;
	    		}
	    		
	    		pairRelationsRoletypeEvent.add(r1.getRoleType() + " " + r2.getRoleType());
	    		relations = relations.replaceAll(r1.getRoleType() + " " + r2.getRoleType(), "");
	    		eventsFromRelationsOther.put(eventId, relations);
	    		
	    		/*eventsFromRelationsOther.remove(eventId);
	    		relationsKeySetEntities.remove(randomNumberGenerated);
	    	*/
	    		subjectIdr1 = relationUtils.getSubObjIdFromRelation(r1, "subject");
	    		subjectIdr2 = relationUtils.getSubObjIdFromRelation(r2, "subject");
	    		objectIdr1 = relationUtils.getSubObjIdFromRelation(r1, "object"); 
	    		objectIdr2 = relationUtils.getSubObjIdFromRelation(r2, "object"); 
	    	
	    		
	    		if (r1.getObject().contains("<event"))
	    			eventAttrib = propertyUtils.getAttributes (objectIdr1);
	    		
	    		if (r1.getSubject().contains("<event"))
	    			eventAttrib = propertyUtils.getAttributes (subjectIdr1);
	    		
	    		if (r2.getObject().contains("<event"))
	    			eventAttrib = propertyUtils.getAttributes (objectIdr2);
	    		
	    		if (r2.getSubject().contains("<event"))
	    			eventAttrib = propertyUtils.getAttributes (subjectIdr2);
	    		
	    	/*	if (eventAttrib.isEmpty())
	    			eventAttrib = propertyUtils.getAttributes (subjectId);*/
	    	}
	    	
	    	
	    	System.out.println(generated);
	    	StringTokenizer tokenEntitySub;
	    	StringTokenizer tokenEntityObj;
	    	String obj;
	    	String sub;
	    	
	    	expandFromRelation (subjectId,objectId,eventAttrib,node);
	    	
	    	
	    //	expandFromEvent ();
	    	
	    	if (type.contains("jgr"))
	    	{
	    		tokenEntitySub = new StringTokenizer (relationSnapshot.getSubject());	
	    		tokenEntityObj = new StringTokenizer (relationSnapshot.getObject());
	    		obj = tokenEntityObj.nextToken();
		        obj = tokenEntityObj.nextToken();
		    	
		        sub = tokenEntitySub.nextToken();
		        sub = tokenEntitySub.nextToken();
	    
	   	 		es2.setKeywords(sub);
	   	 		es2.setRandomSearch(false);
	   	 		es2.setIndexName("souza_eventkg");
	   	 		es2.setLimit(1);
	   	 		es2.run();
	   	 		
	   	 		Map<String,String> resSub = es2.getGenericDocuments();
	   	    
	   	 		String typeResSub = getTypeEntity (resSub);
	   	    
	   	 		es2.setKeywords(obj);
	   	 		es2.setIndexName("souza_eventkg");
	   	 		es2.setLimit(1);
	   	 		es2.run();
	   	 	
	   	 		Map<String,String> resObj = es2.getGenericDocuments();
	   	    
	   	 		String typeResObj = getTypeEntity (resObj);
	   	    
	   	 			/*  if (!typeResObj.contains("Place") && !(typeResSub.contains("Place")))
	   	    		continue;
	   	 			 */
	   	 		StringTokenizer tokenRoleType = new StringTokenizer (relationSnapshot.getRoleType());
	   	 		String role = tokenRoleType.nextToken();
	   	 		role = tokenRoleType.nextToken();
	   	 		while (relationSnapshot.getObject().isEmpty() /*|| roleTypes.contains(role)*/)
	   	 		{
	   	 			while (generatedKeys.contains(generated = random.nextInt(maxint)))
	   	 				generated = random.nextInt(maxint);
	    		
	   	 			es.setIndexName("souza_eventkg_relations");
	   	 			es.setRand(generated);
	   	 			es.run();
	   	 			relationSnapshot = es.getRelation();
		    	
	   	 			try {
	   	 				tokenRoleType = new StringTokenizer (relationSnapshot.getRoleType());
	   	 				role = tokenRoleType.nextToken();
	   	 				role = tokenRoleType.nextToken();
	   	 			} catch (Exception e)
	   	 			{
	   	 				System.out.print("");
	   	 			}
	   	 		}
	   	 		generatedRelationKeys.add(relationSnapshot.getId());
	   	 		//System.out.println(relationSnapshot.getId() + "sub: " + relationSnapshot.getSubject() +" obj: " + relationSnapshot.getObject() );
	   	 		totalGenerated++;
	    	
	   	 		if (totalGenerated%500 == 0)
	   	 			System.out.println(("current Generated : "+totalGenerated + " of " + MAX_KEYS));
	   	 		generatedRelations.put(relationSnapshot.getId(),relationSnapshot);
	    	
	   	 		roleTypes.add(role);
	   	 		
	    	}
	        
	      
	  //  	System.out.println(totalGenerated);
	    	}	
	}
	
	
	private static void expandFromRelation (String subId, String obId, String eventAttrib, String node) throws IOException, InterruptedException
	{
		QueryUtils queryUtils;
	  if (node.contains("entity"))	
		 queryUtils = new QueryUtils (relationSnapshot, eventAttrib, obId, subId, timeStampUtils, relationUtils, 
				propertyUtils,es,eventsFromRelationsOther, pairRelationsRoletypeEntity);
	  else
		   queryUtils = new QueryUtils (r1,r2, eventAttrib, objectIdr1, objectIdr2,subjectIdr1, subjectIdr2, timeStampUtils, relationUtils, 
					propertyUtils,es,eventsFromRelationsOther); 
	  
		HashSet<String> queriesCreated = new HashSet<String> ();
		
		/*if (node.contains("entity"))
			query = queryUtils.getQueryFirstRelation();
		*/
		query = queryUtils.getQuerySubGraph(node);
		
		if (node.contains("entity"))
			pairRelationsRoletypeEntity = queryUtils.getPairRelationsRoletypeEntity();
		
		if (!query.isEmpty() && query.length()>400)
		{
			sb.append("Query number:" + generated + "\n");
			sb.append(query + "\n");
			queriesCreated.add(query); 
			generated ++;
		}
				
	}
	
	private static String getTypeEntity (Map<String,String> genericDocuments)
	{
		
		String typeEntity = "";
		 for (Entry<String, String> document : genericDocuments.entrySet())
			{
			 
			    String value = document.getValue();
				StringTokenizer tokenLine = new StringTokenizer (value, "\n");
				
				while (tokenLine.hasMoreTokens())
				{
					String currentLine = tokenLine.nextToken();

					if (currentLine.contains("rdf:type"))
					{
						StringTokenizer tokenType = new StringTokenizer (currentLine);
						typeEntity = tokenType.nextToken();
						typeEntity = tokenType.nextToken();
					}
					
				}
				
			 
			}
		 
		 return typeEntity;
		
	}
	
}
	
