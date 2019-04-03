package de.l3s.souza.EventKG.queriesGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import de.l3s.souza.EventKG.graphGenerator.IndexRelations;
import de.l3s.souza.EventKG.queriesGenerator.relation.RelationSnapshot;
import de.l3s.souza.EventKG.queriesGenerator.relation.RelationUtils;


public class RandomWalk {
	
	private static int maxqueries;
	private static int generated;
	private static RandomFilter randomFilter;
	private static HashSet<Integer> generatedKeys;
	private static TimeStampUtils timeStampUtils;
	private static PropertyUtils propertyUtils;
	private static RelationUtils relationUtils;
	private static QueryUtils queryUtils;
	private static String queryType; 
	private static RelationSnapshot r1;
	private static RelationSnapshot r2;
	
	private static IndexRelations indexes;
	
	private static Map<String,String> typesEntities;
	private static String objectIdr1 = "";
	private static String objectIdr2 = "";
	private static String subjectId = "";
	private static String subjectIdr1 = "";
	private static String subjectIdr2 = "";
	
	private static HashSet<String> pairRelationsRoletypeEvent; 

	private static ArrayList<String> queriesCreatedHuman ;
	private static ArrayList<String> queriesCreated ;

	public static int getMaxqueries() {
		return maxqueries;
	}

	public static void setMaxqueries(int maxqueries) {
		RandomWalk.maxqueries = maxqueries;
	}

	public RandomWalk (int maxqueries, IndexRelations indexes, Map<String,String> typesEntities) {
	
		queryType = "";
		queriesCreated = new ArrayList<String> ();
		queriesCreatedHuman = new ArrayList<String> ();
		RandomWalk.typesEntities = typesEntities;
		this.indexes = indexes;
		generated = 0;
		pairRelationsRoletypeEvent = new HashSet<String>();
		RandomWalk.maxqueries = maxqueries;
		randomFilter = new RandomFilter ();
		generatedKeys = new HashSet<Integer>();
		propertyUtils = new PropertyUtils ();
    	timeStampUtils = new TimeStampUtils (propertyUtils);
    	relationUtils = new RelationUtils (propertyUtils);
    	queryUtils = new QueryUtils ();
	}

	public void generateQueries () throws IOException, InterruptedException
	{
		
		 int maxint = 0;
		 
		 int randomNumberGenerated = 0;
		 ArrayList<String> nodeTypes = new ArrayList<String> ();
		 String node;
		 nodeTypes.add("entity");
		 nodeTypes.add("event");
		
		 Random random = new Random (System.currentTimeMillis());
		 System.out.print("Generating keys...");
 
		 while (!(indexes.getRelationIndex().isEmpty()) && (!indexes.getRelationsKeySetEntities().isEmpty()) && generated  < maxqueries)
		 {
			 String eventId = "";
			 String relationId1="";
	    	 String relationId2="";
	    	 String	roletyper1="";
	    	 String	roletyper2="";
			 node = randomFilter.getRandomValue(nodeTypes);
			
			 if (indexes.getEvents().isEmpty())
			 	node = "entity";
		
			 maxint = indexes.getRelationsKeySetEntities().size();

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
	    		
	    		System.out.print("+++ event nodes left +++: " + indexes.getRelationsKeySetEntities().size() + "\n");
	    		
	    		eventId = indexes.getRelationsKeySetEntities().get(randomNumberGenerated);

	    		String relations = indexes.getEvents().get(eventId);
	    		StringTokenizer tokenRelations;
	    		if (relations!=null)
	    		{
	    			if (!relations.isEmpty())
	    			 tokenRelations = new StringTokenizer (relations);
	    			else
	    			{
	    				indexes.removeRelationKeySetEntities((randomNumberGenerated));
	    				continue;
	    				
	    			}
	    		}
	    		else
	    		{
	    			indexes.removeRelationKeySetEntities((randomNumberGenerated));
	    			continue;
	    		}
	    		
	    		queryType = queryUtils.generateQueryType();
	    		
	    		try {
	    			
	    			relationId1 = tokenRelations.nextToken();
	    			
	    			r1 = indexes.getRelationIndex().get(relationId1);
	    		
	    			r1.setId(relationId1);
	    			
	    			subjectIdr1 = relationUtils.getSubObjIdFromRelation(r1, "subject");
		    		objectIdr1 = relationUtils.getSubObjIdFromRelation(r1, "object"); 
	    			
		    		if (subjectIdr1.contains("entity"))
		    		{
			    		relationsEntity = indexes.getEntities().get(subjectIdr1);
			    		
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
			    				indexes.removeRelationKeySetEntities(randomNumberGenerated);
			    				continue;
			    			}
		    			}
		    			else
		    			{
		    				indexes.removeRelationKeySetEntities(randomNumberGenerated);
		    				continue;
			    		
		    			}
			    		r2 = indexes.getRelationsEntitiesOtherIndex().get(relationId2);
			    	
			    		r2.setId(relationId2);
			    		
			    		indexes.removeFromEntitiesOtherIndex(relationId2);
			    		
			    		indexes.removeFromRelationIndex(relationId1);

		    		}
		    		else if (objectIdr1.contains("entity"))
		    		{
		    			relationsEntity = indexes.getEntities().get(objectIdr1);
		    			
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
			    				indexes.removeRelationKeySetEntities(randomNumberGenerated);
			    				continue;
			    			}
		    			}
		    			else
		    			{
		    				indexes.removeRelationKeySetEntities(randomNumberGenerated);
		    				continue;
		    			}
		    			r2 = indexes.getRelationsEntitiesOtherIndex().get(relationId2);
		    			
		    			r2.setId(relationId2);
		    			
		    			indexes.removeFromRelationIndex(relationId1);
		    			
		    			indexes.removeFromEntitiesOtherIndex(relationId2);
		    			
		    			
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
			    			
		    				indexes.updateEvents(eventId, relations);
		    				
		    				indexes.updateEntitiesIndex(entityId, relationsEntity);
		    				
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
		    			
		    			indexes.updateEvents(eventId, relations);
	    				
	    				indexes.updateEntitiesIndex(entityId, relationsEntity);
		    			
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
		    				indexes.removeEvent(eventId);
		    			
		    			if  (relationsEntity.isEmpty())
		    				indexes.removeEntity(entityId);
		    		
		    		} //end if (entityFound)
		    		
	    		} catch (Exception e)
    			{
	    			indexes.removeEvent(eventId);
    				indexes.removeRelationKeySetEntities(randomNumberGenerated);

    				continue;
	    			
    			}
	    	}
	    	
	    	if (node.contains("event"))
	    	{
	    		
	    		System.out.print("+++ event nodes left+++: " + indexes.getRelationsKeySetEntities().size() + "\n");
	    		
	    		eventId = indexes.getRelationsKeySetEntities().get(randomNumberGenerated);
	    		
	    		String relations = indexes.getEvents().get(eventId);
	    		StringTokenizer tokenRelations;
	    		
	    		if (relations!=null)
	    		{
	    			if (!relations.isEmpty())
	    			 tokenRelations = new StringTokenizer (relations);
	    			else
	    			{
	    				indexes.removeRelationKeySetEntities(randomNumberGenerated);
	    				continue;
	    				
	    			}
	    		}
	    		else
	    		{
	    			indexes.removeRelationKeySetEntities(randomNumberGenerated);
	    			continue;
	    		}
	    		
	    		queryType = queryUtils.generateQueryType();
	    
	    		try {
	    			
	    			 relationId1 = tokenRelations.nextToken();
	    			 relationId2 = tokenRelations.nextToken();
	    		
	    			r1 = indexes.getRelationIndex().get(relationId1);
	    			r2 = indexes.getRelationIndex().get(relationId2);
	    			r1.setId(relationId1);
	    			
	    			indexes.removeFromRelationIndex(relationId1);
	    			indexes.removeFromRelationIndex(relationId2);
	    			
	    			r2.setId(relationId2);
	    		} catch (Exception e)
	    		{
	    			//not enough relations left to construct a query, then delete the event node
	       			indexes.removeEvent(eventId);
		    		indexes.removeRelationKeySetEntities(randomNumberGenerated);
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
	    			
	    			indexes.updateEvents(eventId, relations);
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
    			
    			indexes.updateEvents(eventId, relations);
	    		
    			indexes.removeEvent(eventId);
	    		indexes.removeRelationKeySetEntities(randomNumberGenerated);
	    	
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
	    	//expandFromRelation (subjectId,objectId,eventAttrib,node);
	    	
	    	
	    	pairRelationsRoletypeEvent.add(roletyper1 + " " + roletyper2);
	    	
	    	}	
	}

	/*private static void expandFromRelation (String subId, String obId, String eventAttrib, String node) throws IOException, InterruptedException
	{
		QueryUtils queryUtils;
		QueryCleaner cleanQuery = new QueryCleaner ();
		HumanReadableQueryManager humanReadableQuery = new HumanReadableQueryManager ();
		
		queryUtils = new QueryUtils (r1,r2, eventAttrib, objectIdr1, objectIdr2,subjectIdr1, subjectIdr2, timeStampUtils, relationUtils, 
					propertyUtils,indexes.getEvents(),queryType,typesEntities,generated); 
	  	
		//queryType = queryUtils.getQueryType();
		
		String query = queryUtils.getQuerySubGraph(node);
		
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
*/
}
