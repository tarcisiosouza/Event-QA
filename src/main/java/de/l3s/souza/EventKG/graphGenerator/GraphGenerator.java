package de.l3s.souza.EventKG.graphGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;

import de.l3s.elasticquery.ElasticMain;
public class GraphGenerator 
{
	private static String relation = "";
	private static ElementVertex evRelation = new ElementVertex ();
	private static ElementVertex evSubject  = new ElementVertex ();
	private static ElementVertex evObject = new ElementVertex ();
	private static boolean isRelationTemporal = false;
	private static String predicateBegintimestamp = "";
	private static String predicateEndtimestamp = "";
	private static String beginTimestamp = "";
	private static String endTimestamp = "";
	private static String roleTypeString = "";
	private static String subRelation = "";
	private static String predicateRelationObject = "";
	private static String predicateRelationSubject = "";
	private static String lineRelationsOther = "";
	private static String lineRelationsEntitiesTemporal = "";
	private static String objRelation = "";
	private static ElasticMain es;
	private static ElasticMain es2;
	
	

	private static Map<String,ElementVertex> idVertex = new HashMap<String,ElementVertex> ();
	private static Graph<ElementVertex, RelationshipEdge> EventKG = new Multigraph<ElementVertex, RelationshipEdge>(RelationshipEdge.class);
	private static HashMap<String,Entity> entities = new HashMap<String,Entity>();
	private static Map<String,String> genericDocuments = new HashMap<String,String>();
	private static Map<String,String> entitiesEventsTimestamps = new HashMap<String,String>();
	private static HashMap<String,Event> events = new HashMap<String,Event>();
	private static HashSet<String> entity = new HashSet<String>();
    public static void main( String[] args ) throws IOException
    {
    	File fileEntities = new File ("/home/souza/EventKG/data/output/relations_base.nq");
    	FileReader frEntities = new FileReader (fileEntities);
    	BufferedReader brEntities = new BufferedReader (frEntities);
    	
    	File fileRelationsOther = new File ("/home/souza/EventKG/data/output/relations_other.nq");
    	FileReader frRelationsOther = new FileReader (fileRelationsOther);
    	BufferedReader brRelationsOther = new BufferedReader (frRelationsOther);
    	
    	File fileRelationsEntitiesTemporal = new File ("/home/souza/EventKG/data/output/relations_entities_temporal.nq");
    	FileReader frRelationsEntitiesTemporal = new FileReader (fileRelationsEntitiesTemporal);
    	BufferedReader brRelationsEntitiesTemporal = new BufferedReader (frRelationsEntitiesTemporal);
    	
    	es = new ElasticMain("", 1, "entityid","souza_eventkg");
    	es2 = new ElasticMain("", 1, "entityid","souza_eventkg_timestamp");

    	String lineEntities = "";
    	
    	String currentEntityID = "";
    	String predicate = "";
    	String dbpedia = "";
    	int i = 0;
      
    	int relations = 0;
    	while ((lineEntities=brEntities.readLine())!=null)
    	{
    		i++;
    		if (lineEntities.contains("@prefix") || lineEntities.contains("@base") || 
    				!(lineEntities.contains("eventKG-g:event_kg") || !(lineEntities.contains("eventKG-g:"))))
    			continue;
    		
    		//System.out.println (i);
    		StringTokenizer tokenEntities = new StringTokenizer (lineEntities);
    		while (tokenEntities.hasMoreElements())
    		{
    			String subject = "";
    			
    			String object = "";
    			String kg = "";
    		  //try {	
    			subject = tokenEntities.nextToken();
    			predicate = tokenEntities.nextToken();
    			object = tokenEntities.nextToken();
    			kg = tokenEntities.nextToken();
    		/*  } catch (Exception e)
    		  {
    			  System.out.print("");
    		  }
    		 */
    			if (subject.contains("event") && object.contains("entity") || (subject.contains("event") && object.contains("event")))
    			{
    				relations ++;
    				
    				/*if (relations>50000)
    					break;*/
    				
    				createVertexEdge (subject,object,predicate);
    				
    				//String relation = getLabelEdge (EventKG,subjectWithAttributes,objectWithAttributes);
    				//System.out.println(relation);
    				break;
    				
    				
    			}
    			
    		/*	if (predicate.contains("hasBeginTimeStamp") || predicate.contains("hasEndTimeStamp"))
    			{
    				entitiesEventsTimestamps.put(subject, lineEntities.replaceAll(subject, ""));
    				//System.out.println("hasbegintimestamp");
    				break;
    				//add the attributes to entities and events nodes (create a map for each predicate to add the attributes later)
    			}
    			*/
    			//for all predicates, update the graph nodes with the attributes
    			
    			if (subject.contains("entity") && subject.contains("entity"))
    			{
    				createVertexEdge (subject,object,predicate);
    				break;
    				//create a connection between two entities (update the graph)
    			}
    				
    			if (predicate.contains("nextEvent") || predicate.contains("previousEvent") || predicate.contains("hasSubEvent"))
    			{
    				createVertexEdge (subject,object,predicate);
    				break;
    				//if nextEvent, previousEvent or hasSubEvent, create node connected with the subject
    			}
    			
    		}
    		
    		
        //	System.out.println (entity.size());
    	}
    	
    	while ((lineRelationsOther=brRelationsOther.readLine())!=null)
    	{
    		
    		if (lineRelationsOther.contains("@prefix") || lineRelationsOther.contains("@base") || lineRelationsOther.isEmpty() )
    			continue;
    		
    		roleTypeString="";
    		evRelation = new ElementVertex ();
    		evSubject = new ElementVertex ();
    		evObject = new ElementVertex ();
    		subRelation = "";
    		objRelation="";
    		StringTokenizer tokenRelations = new StringTokenizer (lineRelationsOther);
    		
    		addRelationNodes (tokenRelations,lineRelationsOther);
    		
    	}
    	
    	isRelationTemporal = true;
    	
    	while ((lineRelationsEntitiesTemporal=brRelationsEntitiesTemporal.readLine())!=null)
    	{
    		
    		if (lineRelationsEntitiesTemporal.contains("@prefix") || lineRelationsEntitiesTemporal.contains("@base") || lineRelationsEntitiesTemporal.isEmpty() )
    			continue;
    		roleTypeString="";
    		evRelation = new ElementVertex ();
    		evSubject = new ElementVertex ();
    		evObject = new ElementVertex ();
    		subRelation = "";
    		objRelation="";
    		StringTokenizer tokenRelations = new StringTokenizer (lineRelationsEntitiesTemporal);
    		
    		addRelationNodes (tokenRelations,lineRelationsEntitiesTemporal);
    		
    	}
    	
    	System.out.println("Vertices: " + EventKG.vertexSet().size());

    }
    
    private static void addRelationNodes (StringTokenizer tokenRelations, String lineRecord) throws IOException
    {
    	
    		if (lineRecord.contains("object"))
			{
				relation = tokenRelations.nextToken();
				predicateRelationObject = tokenRelations.nextToken();
				objRelation = tokenRelations.nextToken();
			
			}
    		
    		if (lineRecord.contains("subject"))
    		{
    			relation = tokenRelations.nextToken();
				predicateRelationSubject = tokenRelations.nextToken();
				subRelation = tokenRelations.nextToken();
    		}
    		
    		if (lineRecord.contains("roleType"))
    		{
    			relation = tokenRelations.nextToken();
    			String predicateRoleTypeOriginal = tokenRelations.nextToken();
    			String roleType = tokenRelations.nextToken();
    			
    			if (roleType.contains("wdt"))
    			{
    				roleTypeString = getWikidataProperty(roleType);
    			}
    			else
    				roleTypeString = roleType;
    			
    			if (roleTypeString.isEmpty())
    				roleTypeString = roleType;
    		}
    		
    		if (!(roleTypeString.isEmpty()) && (!isRelationTemporal))
    		{
    			
    			evRelation = assignNodeAttributes (relation);
				evSubject = assignNodeAttributes (subRelation);
				evObject = assignNodeAttributes (objRelation);
				
    			createVertexEdge(evRelation,evObject,predicateRelationObject);
				createVertexEdge(evRelation,evSubject,predicateRelationSubject);
    		}
    		
    		if (isRelationTemporal && !(roleTypeString.isEmpty()))
    		{
    			
    			if (lineRecord.contains("sem:hasBeginTimeStamp"))
    			{
    				relation = tokenRelations.nextToken();
    				predicateBegintimestamp = tokenRelations.nextToken();
        			beginTimestamp = tokenRelations.nextToken();
        			evRelation = assignNodeAttributes (relation);
        			evRelation.setBeginTimeStamp(beginTimestamp);
        			evSubject = assignNodeAttributes (subRelation);
    				evObject = assignNodeAttributes (objRelation);
    				
        			createVertexEdge(evRelation,evObject,predicateRelationObject);
    				createVertexEdge(evRelation,evSubject,predicateRelationSubject);
    			}
    			
    			if (lineRecord.contains("sem:hasEndTimeStamp"))
    			{
    				relation = tokenRelations.nextToken();
    				predicateEndtimestamp = tokenRelations.nextToken();
        			endTimestamp = tokenRelations.nextToken();
        			
        			if (evRelation!=null)
        				evRelation.setEndTimeStamp(endTimestamp);
        			else
        			{
        				evRelation = assignNodeAttributes (relation);
        				evRelation.setEndTimeStamp(endTimestamp);
        			}
        			
        			evSubject = assignNodeAttributes (subRelation);
    				evObject = assignNodeAttributes (objRelation);
    				
        			createVertexEdge(evRelation,evObject,predicateRelationObject);
    				createVertexEdge(evRelation,evSubject,predicateRelationSubject);
    			}
    			
				
    			
    		}
    }
    private static ElementVertex assignNodeAttributes (String idElement) throws IOException
    {
    
    	ElementVertex output = new ElementVertex ();
    	if (!idElement.contains("relation"))
    	{
    		if (idVertex.containsKey(idElement))
    			output = idVertex.get(idElement);
    		else
    			output = getElementAttributes (idElement);
    	
    	}
    	else
    	{
    		if (idVertex.containsKey(idElement))
    			output = idVertex.get(idElement);
    		else
    		{
    			output.setId(relation);
    			output.setType("relation");
    			output.setRoleType(roleTypeString);
    		}
    	}
    	return output;
    }
    private static String getWikidataProperty (String wdt) throws IOException
    {
    	String wikiProperty = "";
    	es.setKeywords(wdt);
		es.setLimit(1);
		es.run();
		genericDocuments = es.getGenericDocuments();
		for (Entry<String, String> document : genericDocuments.entrySet())
		{
			wikiProperty = document.getValue();
		}
		
		return wikiProperty;
    }
    private static void updateVertexAttribute()
    {
    	
    }
    
    private static String getLabelEdge(Graph<ElementVertex, RelationshipEdge> graph,ElementVertex vertex1,ElementVertex vertex2)
    {
    	return graph.getEdge(vertex1, vertex2).getLabel().toString();
    }
    
    private static ElementVertex getElementAttributes (String elementID) throws IOException {
    	
    	String type = "";
    	String englishLabel = "";
    	String cleanLabel = "";
    	String fullLabel = "";
    	String fullEnglishLabel = "";
    	ArrayList<String> owlValues = new ArrayList<String>();
    	ArrayList<String> labelValues = new ArrayList<String>();
    	ArrayList<String> descriptionValues = new ArrayList<String>();
    	ArrayList<String> timestamps = new ArrayList<String>();


    	ElementVertex ev = new ElementVertex ();
    	es.setKeywords(elementID);
		es.setLimit(1);
		es.run();
		genericDocuments = es.getGenericDocuments();
		
		if (elementID.contains("event"))
			type = "event";
		
		if (elementID.contains("entity"))
			type = "entity";
		
		if (elementID.contains("relation"))
			type = "relation";
		
		ev.setId(elementID);
		ev.setType(type);
		
		for (Entry<String, String> document : genericDocuments.entrySet())
		{
			String value = document.getValue();
			StringTokenizer tokenLine = new StringTokenizer (value, "\n");
			
			while (tokenLine.hasMoreTokens())
			{
				String currentLine = tokenLine.nextToken();
				
				if (currentLine.contains("owl"))
				{
					
					owlValues.add(currentLine);
					/*
					StringTokenizer token = new StringTokenizer (currentLine);
					while (token.hasMoreElements())
					{
						String owl = token.nextToken();
						String valueOwl = token.nextToken();
						String source = token.nextToken();
						
					}
					
					*/
				}
				
				if (currentLine.contains("label"))
				{
					labelValues.add(currentLine);
				}
				
				if (currentLine.contains("description"))
				{
					descriptionValues.add(currentLine);
				}
				
			}
			
			
		}
		
		for (int i=0;i<labelValues.size();i++)
		{
			if (labelValues.get(i).contains("@en"))
			{
				StringTokenizer token = new StringTokenizer (labelValues.get(i));
				
				final Pattern pattern = Pattern.compile("\"(.+?)\"@en");
				final Matcher m = pattern.matcher(labelValues.get(i));
				m.find();
				englishLabel = m.group(1);
				fullEnglishLabel = labelValues.get(i);
				cleanLabel = englishLabel;
				ev.setFullEnglishLabel(fullEnglishLabel);
				ev.setCleanLabel(cleanLabel);
			}
		}
		
		if (englishLabel.isEmpty())
		{
			try {
			final Pattern pattern = Pattern.compile("\"(.+?)\"@(.+?)");
			final Matcher m = pattern.matcher(labelValues.get(0));
			m.find();
			cleanLabel = m.group(1);
			fullLabel = labelValues.get(0);
			ev.setCleanLabel(cleanLabel);
			ev.setFullLabel(fullLabel);
			} catch (Exception e )
			{
				
			}
			
		}
		ev.setDescription(descriptionValues);
		ev.setLabels(labelValues);
		ev.setOwl(owlValues);
		
		es2.setKeywords(elementID);
		es2.setLimit(1);
		es2.run();
		genericDocuments = es2.getGenericDocuments();
		
		for (Entry<String, String> document : genericDocuments.entrySet())
		{
			String value = document.getValue();
			StringTokenizer tokenLine = new StringTokenizer (value, "\n");
			
			while (tokenLine.hasMoreTokens())
			{
				String currentLine = tokenLine.nextToken();
				
				if (currentLine.contains("sem:hasEndTimeStamp"))
				{
					
					StringTokenizer token = new StringTokenizer (currentLine);
					token.nextToken();
					token.nextToken();
					ev.setEndTimeStamp(token.nextToken());
					timestamps.add(currentLine.replaceAll(elementID, ""));
					break;
				}
				
				if (currentLine.contains("sem:hasBeginTimeStamp"))
				{
					
					StringTokenizer token = new StringTokenizer (currentLine);
					token.nextToken();
					
					ev.setBeginTimeStamp(token.nextToken());
					timestamps.add(currentLine);
					break;

				}
				
			
				
			}
			
			
		}
		return ev;
    	
    }
    
    private static void createVertexEdge (String subject, String object, String predicate) throws IOException
    {
    	
    	ElementVertex subjectWithAttributes = new ElementVertex ();
		ElementVertex objectWithAttributes = new ElementVertex ();

		subjectWithAttributes = getElementAttributes (subject);
		objectWithAttributes = getElementAttributes (object);

		try {
		//create connection between subject and object with the predicate
		EventKG.addVertex(subjectWithAttributes);
		EventKG.addVertex(objectWithAttributes);
		EventKG.addEdge(subjectWithAttributes, objectWithAttributes, new RelationshipEdge (predicate));
		idVertex.put(subjectWithAttributes.getId(), subjectWithAttributes);
		idVertex.put(objectWithAttributes.getId(), objectWithAttributes);
		} catch (Exception e )
		{}
    }
    
    private static void createVertexEdge (ElementVertex subject, ElementVertex object, String predicate) throws IOException
    {
    	
    try {
		//create connection between subject and object with the predicate
		EventKG.addVertex(subject);
		EventKG.addVertex(object);
		EventKG.addEdge(subject, object, new RelationshipEdge (predicate));
		idVertex.put(subject.getId(), subject);
		idVertex.put(object.getId(), object);
    } catch (Exception e)
    {}
    }
}
