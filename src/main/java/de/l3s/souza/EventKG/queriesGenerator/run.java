package de.l3s.souza.EventKG.queriesGenerator;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
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
	
	private static BufferedWriter out_machine ;
	private static BufferedWriter out_human ;

	private static HashSet<Integer> generatedKeys = new HashSet<Integer>();
	private static RelationUtils relationUtils;

	private static String query = "";

	private static int generated = 0;
	private static Map<String,String> entitiesEvents = new HashMap<String,String>();
	private static Map<String,String> events = new HashMap<String,String>();
	private static Map<String,String> eventsFromRelationsOther = new HashMap<String,String>();
	private static Map<String,String> entitiesFromRelationsEntitiesOther = new HashMap<String,String>();
	private static RandomFilter randomFilter;

	private static ArrayList<String> relationsKeySetEntities;
	private static Map<String,String> typesEntities = new HashMap<String,String>();
	private static IndexRelations indexRelations;
	private static Map<String,RelationSnapshot> RelationsIndex = new HashMap<String,RelationSnapshot>();
	private static Map<String,RelationSnapshot> RelationsEntitiesOtherIndex = new HashMap<String,RelationSnapshot>();

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
	private static PropertiesManager pm;
	private static ArrayList<String> queriesCreated ;
	private static ArrayList<String> queriesCreatedHuman ;

	public static void main (String args[]) throws IOException, InterruptedException
	{
		
		pm = new PropertiesManager ();
		System.out.println("Reading data...");
		IndexTypes indexTypes = new IndexTypes ();
		typesEntities = indexTypes.getMap(pm.getDataFolder());
		queriesCreated = new ArrayList<String> ();
		queriesCreatedHuman = new ArrayList<String> ();
		
		indexRelations = new IndexRelations ();
		randomFilter = new RandomFilter ();
		r1 = new RelationSnapshot ();
		r2 = new RelationSnapshot ();
	
		RelationsIndex = indexRelations.getNewRelationIndex(pm.getDataFolder()+"relations_other_dbo.nq"," ",pm.getMaxRelations());
		eventsFromRelationsOther = indexRelations.getEvents();
		
		RelationsEntitiesOtherIndex = indexRelations.getNewRelationIndex(pm.getDataFolder()+"relations_entities_other_dbo_filter.nq","<entity_",pm.getMaxRelations());
		entitiesFromRelationsEntitiesOther= indexRelations.getEntities();
	
		System.out.println("Reading completed!");
	
		relationsKeySetEntities = new ArrayList<>(eventsFromRelationsOther.keySet());
		
		out_machine = new BufferedWriter
			    (new OutputStreamWriter(new FileOutputStream("queries_machine.json"),"UTF-8"));
		
		out_human = new BufferedWriter
			    (new OutputStreamWriter(new FileOutputStream("queries_human.html"),"UTF-8"));
		
    	propertyUtils = new PropertyUtils ();
    	timeStampUtils = new TimeStampUtils (propertyUtils);
    	relationUtils = new RelationUtils (propertyUtils);
    	
    	generateQueries (pm.getMaxQueries());
    	
		 System.out.print("keys generated! Building queries...");
		 
		 JsonQueryManager jsonQuery = new JsonQueryManager ();
		HumanReadableQueryManager humanReadableQuery = new HumanReadableQueryManager ();

		 out_machine.write(jsonQuery.getHeadJson());
		 out_human.write(humanReadableQuery.getHtmlHead());
		 for (int i=0;i<queriesCreated.size();i++)
		 {
			 String currentQuery = queriesCreated.get(i);
			 if (i+1==queriesCreated.size())
			 {
				 currentQuery = currentQuery.replaceAll("},", "}");
				 out_machine.write(currentQuery);
			 }
			 else
			 {
				 out_machine.write(currentQuery);
			 }
			 
			;
			 
			 out_human.write( queriesCreatedHuman.get(i)  );
				 
		 }
		
		 out_machine.write(jsonQuery.getEndJson());
		 out_human.write(humanReadableQuery.getHtmlEnd());
		 out_machine.close();
		 out_human.close();
		 System.out.print("Finished!");

	
	}
	
	private static void generateQueries (int maxqueries) throws IOException, InterruptedException
	{
		
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
		QueryCleaner cleanQuery = new QueryCleaner ();
		HumanReadableQueryManager humanReadableQuery = new HumanReadableQueryManager ();
		
		queryUtils = new QueryUtils (r1,r2, eventAttrib, objectIdr1, objectIdr2,subjectIdr1, subjectIdr2, timeStampUtils, relationUtils, 
					propertyUtils,eventsFromRelationsOther,queryType,typesEntities,generated); 
	  	
		queryType = queryUtils.getQueryType();
		
		query = queryUtils.getQuerySubGraph(node);
		
		JsonQueryManager jsonQueryManager = new JsonQueryManager ();
		
		if (!query.isEmpty() && query.length()>400)
		{
			
			generated ++;
			query = query.replaceAll("LIMIT 1", "");
			
			String queryHumanReadable = query;
			queryHumanReadable = cleanQuery.getCleanQuery(queryHumanReadable);
			queryHumanReadable = humanReadableQuery.getStringUrlFormat(queryHumanReadable,generated);
			
			queriesCreatedHuman.add(queryHumanReadable);
			
			query = cleanQuery.getCleanQuery(query);
			query = query.replaceAll("\n", "");
			query  = jsonQueryManager.getJsonQuery(query, generated);
			
			queriesCreated.add(query); 
		
		}
				
	}
	
}
	
