package de.l3s.souza.EventKG.queriesGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.StringTokenizer;
import de.l3s.souza.EventKG.graphGenerator.IndexRelations;
import de.l3s.souza.EventKG.graphGenerator.IndexTypes;
import de.l3s.souza.EventKG.queriesGenerator.relation.RelationSnapshot;
import de.l3s.souza.EventKG.queriesGenerator.relation.RelationUtils;
import de.l3s.souza.properties.PropertiesManager;

public final class run {

	private static HashSet<String> pairRelationsRoletypeEvent = new HashSet<String>(); 
	private static HashSet<String> pairRelationsRoletypeEntity ;

	private static BufferedWriter out ;

	private static HashSet<Integer> generatedKeys = new HashSet<Integer>();
	private static RelationUtils relationUtils;

	private static String query = "";

	private static StringBuilder sb ;
	private static int generated = 0;
	private static Map<String,String> entitiesEvents = new HashMap<String,String>();
	private static Map<String,String> events = new HashMap<String,String>();
	private static Map<String,String> eventsFromRelationsOther = new HashMap<String,String>();
	private static Map<String,String> entitiesFromRelationsEntitiesOther = new HashMap<String,String>();
	private static RandomFilter randomFilter;

	private static ArrayList<String> relationsKeySet;
	private static ArrayList<String> relationsKeySetEntities;
	private static Map<String,String> typesEntities = new HashMap<String,String>();
	private static IndexRelations indexRelations;
	private static Map<String,RelationSnapshot> RelationsIndex = new HashMap<String,RelationSnapshot>();
	private static Map<String,RelationSnapshot> RelationsEntitiesOtherIndex = new HashMap<String,RelationSnapshot>();

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
	private static String queryType;

	public static void main (String args[]) throws IOException, InterruptedException
	{
		PropertiesManager pm = new PropertiesManager ();
		
		System.out.println("Reading data...");
		IndexTypes indexTypes = new IndexTypes ();
		typesEntities = indexTypes.getMap(pm.getDataFolder());
		
		indexRelations = new IndexRelations ();
		randomFilter = new RandomFilter ();
		r1 = new RelationSnapshot ();
		r2 = new RelationSnapshot ();
		pairRelationsRoletypeEntity = new HashSet<String>();
	
		RelationsIndex = indexRelations.getNewRelationIndex(pm.getDataFolder()+"relations_other_dbo.nq"," ",pm.getMaxRelations());
		eventsFromRelationsOther = indexRelations.getEvents();
		
		RelationsEntitiesOtherIndex = indexRelations.getNewRelationIndex(pm.getDataFolder()+"relations_entities_other_dbo_filter.nq","<entity_",pm.getMaxRelations());
		entitiesFromRelationsEntitiesOther= indexRelations.getEntities();
	
		System.out.println("Size rel: " + RelationsIndex.size());
		System.out.println("Reading completed!");
		
		File fileWd = new File (pm.getDataFolder()+"property_labels.nq");
		FileReader fileWdR = new FileReader (fileWd);
		BufferedReader brFileWdR = new BufferedReader (fileWdR);
		/*
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
	*/
		relationsKeySet = new ArrayList<>(RelationsIndex.keySet());
		relationsKeySetEntities = new ArrayList<>(eventsFromRelationsOther.keySet());
		sb = new StringBuilder ();
		out = new BufferedWriter
			    (new OutputStreamWriter(new FileOutputStream("queries.nq"),"UTF-8"));
		
	//	es = new ElasticMain("souza_eventkg", 1);
    //	es2 = new ElasticMain("", 1, "entityid","souza_eventkg");
   // 	es.setIndexName("souza_eventkg");
		
    	propertyUtils = new PropertyUtils ();
    	timeStampUtils = new TimeStampUtils (propertyUtils);
    	relationUtils = new RelationUtils (propertyUtils);
    	
    	generateQueries (pm.getMaxQueries());
    	
		 System.out.print("keys generated! Building queries...");
		 
		 out.write (sb.toString());
		 out.close();

		 System.out.print("Finished!");

	
	}
	
	private static void generateQueries (int maxqueries) throws IOException, InterruptedException
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
		 
		 while (!(RelationsIndex.isEmpty()) && (!relationsKeySetEntities.isEmpty()) && generated  < maxqueries)
		 {
			 String eventId = "";
			 String relationId1="";
	    	 String relationId2="";
	    	 String	roletyper1="";
	    	 String	roletyper2="";
			 node = randomFilter.getRandomValue(nodeTypes);
			
			 if (eventsFromRelationsOther.isEmpty())
			 	node = "entity";
		
			 maxint = relationsKeySetEntities.size();

			 randomNumberGenerated = random.nextInt(maxint);
			
			generatedKeys.add(randomNumberGenerated);
		
	    	String objectId = "";
    		String entityId = "";
    		String eventAttrib = "";
    		String relationsEntity = "";
    		boolean entityFound = false;
    		
	    	if (node.contains("entity"))
	    	{
	    		entityFound = true;
	    		
	    		System.out.print("+++ event nodes left+++: " + relationsKeySetEntities.size() + "\n");
	    		
	    		eventId = relationsKeySetEntities.get(randomNumberGenerated);

	    		String relations = eventsFromRelationsOther.get(eventId);
	    		StringTokenizer tokenRelations;
	    		if (relations!=null)
	    		{
	    			if (!relations.isEmpty())
	    			 tokenRelations = new StringTokenizer (relations);
	    			else
	    			{
	    				relationsKeySetEntities.remove(randomNumberGenerated);
	    				continue;
	    				
	    			}
	    		}
	    		else
	    		{
	    			relationsKeySetEntities.remove(randomNumberGenerated);
	    			continue;
	    		}
	    		generateQueryType ();
	    		
	    		try {
	    			
	    			relationId1 = tokenRelations.nextToken();
	    			
	    			r1 = RelationsIndex.get(relationId1);
	    		
	    			r1.setId(relationId1);
	    			
	    			subjectIdr1 = relationUtils.getSubObjIdFromRelation(r1, "subject");
		    		objectIdr1 = relationUtils.getSubObjIdFromRelation(r1, "object"); 
	    			
		    		if (subjectIdr1.contains("entity"))
		    		{
			    		relationsEntity = entitiesFromRelationsEntitiesOther.get(subjectIdr1);
			    		
			    		entityId = subjectIdr1;
			    		
			    		if (relationsEntity!=null)
		    			{
			    			if (!relationsEntity.isEmpty())
			    			{
		    					StringTokenizer tokenRelationsEntity = new StringTokenizer (relationsEntity);
		    					relationId2 = tokenRelationsEntity.nextToken();
			    			}
			    			else

			    			{
			    				relationsKeySetEntities.remove(randomNumberGenerated);
			    				continue;
			    			}
		    			}
		    			else
		    			{
		    				relationsKeySetEntities.remove(randomNumberGenerated);
		    				continue;
			    		
		    			}
			    		r2 = RelationsEntitiesOtherIndex.get(relationId2);
			    	
			    		r2.setId(relationId2);
			    		
			    		RelationsEntitiesOtherIndex.remove(relationId2);
			    		
			    		RelationsIndex.remove(relationId1);

		    		}
		    		else if (objectIdr1.contains("entity"))
		    		{
		    			relationsEntity = entitiesFromRelationsEntitiesOther.get(objectIdr1);
		    			
		    			entityId = objectIdr1;
		    			
		    			if (relationsEntity!=null)
		    			{
			    			if (!relationsEntity.isEmpty())
			    			{
		    					StringTokenizer tokenRelationsEntity = new StringTokenizer (relationsEntity);
		    					relationId2 = tokenRelationsEntity.nextToken();
			    			}
			    			else
			    			{
			    				relationsKeySetEntities.remove(randomNumberGenerated);
			    				continue;
			    			}
		    			}
		    			else
		    			{
		    				relationsKeySetEntities.remove(randomNumberGenerated);
		    				continue;
		    			}
		    			r2 = RelationsEntitiesOtherIndex.get(relationId2);
		    			
		    			r2.setId(relationId2);
		    			
		    			RelationsIndex.remove(relationId1);
		    			
		    			RelationsEntitiesOtherIndex.remove(relationId2);
		    			
		    			
		    		}
		    		else
		    		{
		    			node = "event";
		    	//		eventsFromRelationsOther.remove(eventId);
	    		//		relationsKeySetEntities.remove(randomNumberGenerated);
	    				
		    			entityFound = false;
		    		//	continue;
		    		}
		    		
		    		if (entityFound)
		    		{
		    			StringTokenizer tokenr1 = new StringTokenizer (r1.getRoleType());
		    			roletyper1 = tokenr1.nextToken();
		    			roletyper1 = tokenr1.nextToken();
		    		
		    			StringTokenizer tokenr2 = new StringTokenizer (r2.getRoleType());
		    			roletyper2 = tokenr2.nextToken();
		    			roletyper2 = tokenr2.nextToken();
		    		
		    			if (roletyper1.contentEquals(roletyper2))
		    				continue;
		    		
		    			if (pairRelationsRoletypeEvent.contains(roletyper1 + " " + roletyper2))
		    			{
		    				if (relations.length() > 34)
			    				relations = relations.replaceAll(relationId1 + " ", "");
			    			else
			    				relations = relations.replaceAll(relationId1, "");
			    			
			    			if (relationsEntity.length() > 34)
			    				relationsEntity = relationsEntity.replaceAll(relationId2 + " ", "");
			    			else
			    				relationsEntity = relationsEntity.replaceAll(relationId2, "");
			    			
		    				eventsFromRelationsOther.put(eventId, relations);
		    				
		    				entitiesFromRelationsEntitiesOther.put(entityId, relationsEntity);
		    				
		    				continue;
		    			}
		    		
		    			pairRelationsRoletypeEvent.add(roletyper1 + " " + roletyper2);
		    			
		    			if (relations.length() > 34)
		    				relations = relations.replaceAll(relationId1 + " ", "");
		    			else
		    				relations = relations.replaceAll(relationId1, "");
		    			
		    			if (relationsEntity.length() > 34)
		    				relationsEntity = relationsEntity.replaceAll(relationId2 + " ", "");
		    			else
		    				relationsEntity = relationsEntity.replaceAll(relationId2, "");
		    			
		    			eventsFromRelationsOther.put(eventId, relations);
		    			entitiesFromRelationsEntitiesOther.put(entityId, relationsEntity);
		    			
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
		    		
		    			
		    			if (relations.isEmpty())
		    				eventsFromRelationsOther.remove(eventId);
		    			
		    			if  (relationsEntity.isEmpty())
		    				entitiesFromRelationsEntitiesOther.remove(entityId);
		    		
		    		} //end if (entityFound)
		    		
	    		} catch (Exception e)
    			{
	    			eventsFromRelationsOther.remove(eventId);
    				relationsKeySetEntities.remove(randomNumberGenerated);

    			
    				continue;
	    			
    			}
	    	}
	    	
	    	if (node.contains("event"))
	    	{
	    		
	    		System.out.print("+++ event nodes left+++: " + relationsKeySetEntities.size() + "\n");
	    		

	    		eventId = relationsKeySetEntities.get(randomNumberGenerated);
	    		
	    		String relations = eventsFromRelationsOther.get(eventId);
	    		StringTokenizer tokenRelations;
	    		
	    		if (relations!=null)
	    		{
	    			if (!relations.isEmpty())
	    			 tokenRelations = new StringTokenizer (relations);
	    			else
	    			{
	    				relationsKeySetEntities.remove(randomNumberGenerated);
	    				continue;
	    				
	    			}
	    		}
	    		else
	    		{
	    			relationsKeySetEntities.remove(randomNumberGenerated);
	    			continue;
	    		}
	    		
	    		generateQueryType ();
	    
	    		try {
	    			
	    			 relationId1 = tokenRelations.nextToken();
	    			 relationId2 = tokenRelations.nextToken();
	    		
	    			r1 = RelationsIndex.get(relationId1);
	    			r2 = RelationsIndex.get(relationId2);
	    			r1.setId(relationId1);
	    			RelationsIndex.remove(relationId1);
	    			RelationsIndex.remove(relationId2);

	    			r2.setId(relationId2);
	    		} catch (Exception e)
	    		{
	    			//not enough relations left to construct a query, then delete the event node
	    			

	    			eventsFromRelationsOther.remove(eventId);
		    		relationsKeySetEntities.remove(randomNumberGenerated);
	    			continue;
	    		}
	    	
	    		StringTokenizer tokenr1 = new StringTokenizer (r1.getRoleType());
	    		roletyper1 = tokenr1.nextToken();
	    		roletyper1 = tokenr1.nextToken();
	    		
	    		StringTokenizer tokenr2 = new StringTokenizer (r2.getRoleType());
	    		roletyper2 = tokenr2.nextToken();
	    		roletyper2 = tokenr2.nextToken();
	    		
	    		if (roletyper1.contentEquals(roletyper2))
	    			continue;
	    		
	    		if (pairRelationsRoletypeEvent.contains(roletyper1 + " " + roletyper2))
	    		{
	    			if (relations.length() > 34)
	    				relations = relations.replaceAll(relationId1 + " ", "");
	    			else
	    				relations = relations.replaceAll(relationId1, "");
	    			
	    			if (relations.length() > 34)
	    				relations = relations.replaceAll(relationId2 + " ", "");
	    			else
	    				relations = relations.replaceAll(relationId2, "");
	    			
	    			eventsFromRelationsOther.put(eventId, relations);
	    			continue;
	    		}
	    		
	    		pairRelationsRoletypeEvent.add(roletyper1 + " " + roletyper2);
	    		
	    		if (relations.length() > 34)
    				relations = relations.replaceAll(relationId1 + " ", "");
    			else
    				relations = relations.replaceAll(relationId1, "");
    			
    			if (relations.length() > 34)
    				relations = relations.replaceAll(relationId2 + " ", "");
    			else
    				relations = relations.replaceAll(relationId2, "");
    			
	    		eventsFromRelationsOther.put(eventId, relations);
	    		
	    		eventsFromRelationsOther.remove(eventId);
	    		relationsKeySetEntities.remove(randomNumberGenerated);
	    	
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
	    	
	    	System.out.println("generated " + generated);
	    	
	    	
	    	pairRelationsRoletypeEvent.remove(roletyper1 + " " + roletyper2 );
	    	
	    	System.out.print("+++ expansion node: " + node + "\n");
	    	expandFromRelation (subjectId,objectId,eventAttrib,node);
	    	
	    	
	    	pairRelationsRoletypeEvent.add(roletyper1 + " " + roletyper2);
	    	
	    	}	
	}
	
	private static void generateQueryType ()
	{
		ArrayList<String> queryTypes = new ArrayList<String>();
		queryTypes.add("ask");
		queryTypes.add("select");
		queryTypes.add("count");
		queryType = randomFilter.getRandomValue(queryTypes);
	}
	
	private static void expandFromRelation (String subId, String obId, String eventAttrib, String node) throws IOException, InterruptedException
	{
		QueryUtils queryUtils;
		String prefix = "PREFIX eventKG-r: <http://eventKG.l3s.uni-hannover.de/resource/>\n"
				+ "PREFIX eventKG-s: <http://eventKG.l3s.uni-hannover.de/schema/>\n"
				+ "PREFIX eventKG-g: <http://eventKG.l3s.uni-hannover.de/graph/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "PREFIX so: <http://schema.org/>\n"
				+ "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX wdt: <http://www.wikidata.org/prop/direct/>"
				+ "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" + "PREFIX dbr: <http://dbpedia.org/resource/>\n"
				+ "PREFIX dbpedia-de: <http://de.dbpedia.org/resource/>\n"
				+ "PREFIX dcterms: <http://purl.org/dc/terms/>\n" ;

		queryUtils = new QueryUtils (r1,r2, eventAttrib, objectIdr1, objectIdr2,subjectIdr1, subjectIdr2, timeStampUtils, relationUtils, 
					propertyUtils,eventsFromRelationsOther,queryType,typesEntities,generated); 
	  
		HashSet<String> queriesCreated = new HashSet<String> ();
		
		queryType = queryUtils.getQueryType();
		
		query = queryUtils.getQuerySubGraph(node);
		String nl = "" ;
		if (!query.isEmpty())
			nl = queryUtils.getNaturalLanguage();
		
		if (!query.isEmpty() && query.length()>400)
		{
			//query = query.replaceAll("LIMIT 1", "");
			
			query = URLEncoder.encode(prefix + query, "UTF-8") ;
			
		/*	if (!nl.isEmpty())
			{	*/
			nl = "#NaturalLanguage:\n"+nl;
			nl = URLEncoder.encode(nl, "UTF-8") ;
			sb.append("#Query number: " + generated + "\n");
			sb.append(query + "\n");
			
			sb.append(nl+"\n");
			queriesCreated.add(query); 
			generated ++;
			//}
		}
				
	}
	
}
	
