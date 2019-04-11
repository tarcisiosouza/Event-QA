package de.l3s.souza.EventKG.queriesGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.l3s.souza.EventKG.queriesGenerator.nlp.NlpConversion;
import de.l3s.souza.EventKG.queriesGenerator.nlp.TimeFilterSnapshot;
import de.l3s.souza.EventKG.queriesGenerator.relation.RelationSnapshot;
import de.l3s.souza.EventKG.queriesGenerator.relation.RelationUtils;

public class QueryBuilder {

	private String queryType;
	private String eventAttributes;
	private String selectClause;
	private String eventVar;
	private String queryWithoutTs;
	private String eventVar2;
	private String eventVar3;
	private String relationMentionsVar;
	private String sumRelationsVar;
	private String startTimeVar;
	private String endTimeVar;
	private String entityVar;
	private String entityVar2;
	private String entityVar3;
	private String entityVar4;
	private String relationVar;
	private String relationVar2;
	private String entityLabel;
	private String eventLabel;
	private String object;
	private String last;
	private String subject;
	private String eventLabelVar;
	private String beginTimeStampPred;
	private String beginTimeStampValue;
	private String endTimeStampPred;
	private String endTimeStampValue;
	private String filterTime;
	private String Prefix;
	private RelationSnapshot relation;
	private RelationSnapshot relation1;
	private RelationSnapshot relation2;
	private RandomFilter randomFilter;
	private String query;
	private String beginYearTimeStamp;
	private String endYearTimeStamp;
	private String yearSelected;
	private RelationUtils relationUtils;
	private PropertyUtils propertyUtils;
	private EventkgClient client;
	private ArrayList<String> queries;
	private String queryName;
	private int queryNumber;
	private TimeStampUtils timestampUtils;
	private HashMap<String,String> assignedVars;
	private HashMap<String,String> givenVars;
	private HashMap<String,String> allVars;
	private NlpConversion nlp;
	private Map<String,String> typesEntities;
	private int generated;
	private ArrayList<String> variables = new ArrayList<String>();
	private ArrayList<String> candidateVariables = new ArrayList<String>();
	private ArrayList<String> triples = new ArrayList<String>();
	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public QueryBuilder(RelationUtils relationUtils, RelationSnapshot relation, PropertyUtils propertyUtils, 
			TimeStampUtils timestampUtils, String eventAttributes, String queryType,Map<String,String> typesEntities,int generated) {
		
		this.typesEntities = typesEntities;
		
		this.generated = generated;
		eventVar = "?event";
		this.eventAttributes = eventAttributes;
		queryWithoutTs = "";
		eventVar2 = "?event2";
		relationMentionsVar = "?RelationMentions";
		sumRelationsVar = "?sumRelationMentions";
		startTimeVar = "?startTimeEvent";
		endTimeVar = "?endTimeEvent";
		entityVar= "?entity1";
		entityVar3= "?entity3";
		entityVar4= "?entity4";
		entityVar2= "?place";
		relationVar = "?relation";
		relationVar2 = "?relation2";
		entityLabel = "";
		eventLabel = "";
		queryName = "";
		last = "20";
		eventLabelVar="?eventLabel";
		beginTimeStampPred = "";
		beginTimeStampValue = "";
		filterTime = "";
		randomFilter = new RandomFilter ();
		query = "";
		queryNumber = 0;
		
		yearSelected  = "";
		this.relationUtils = relationUtils;
		this.propertyUtils = propertyUtils;
		this.timestampUtils = timestampUtils;
		this.queryType = queryType;
		this.relation = relation;
		client = new EventkgClient ();
		queries = new ArrayList<String>();
		assignedVars = new HashMap<String,String>();
		givenVars = new HashMap<String,String>();
		allVars = new HashMap<String,String>();

	}

	public String getQueryType() {
		return queryType;
	}

	public QueryBuilder(RelationUtils relationUtils, RelationSnapshot relation1, RelationSnapshot relation2, PropertyUtils propertyUtils, 
			TimeStampUtils timestampUtils, String eventAttributes, String queryType,Map<String,String> typesEntities,int generated, Map<String,String> entitiesEvents) {
		
		this.typesEntities = typesEntities;
		
		this.generated = generated;
		eventVar = "?event";
		this.relation1 = relation1;
		this.relation2 = relation2;
		this.eventAttributes = eventAttributes;
		queryWithoutTs = "";
		eventVar2 = "?event2";
		relationMentionsVar = "?RelationMentions";
		sumRelationsVar = "?sumRelationMentions";
		startTimeVar = "?startTimeEvent";
		endTimeVar = "?endTimeEvent";
		entityVar= "?entity1";
		entityVar3= "?entity3";
		entityVar4= "?entity4";
		entityVar2= "?place";
		relationVar = "?relation";
		relationVar2 = "?relation2";
		entityLabel = "";
		eventLabel = "";
		queryName = "";
		last = "20";
		eventLabelVar="?eventLabel";
		beginTimeStampPred = "";
		beginTimeStampValue = "";
		filterTime = "";
		randomFilter = new RandomFilter ();
		query = "";
		queryNumber = 0;
		
		yearSelected  = "";
		this.relationUtils = relationUtils;
		this.queryType = queryType;
		this.propertyUtils = propertyUtils;
		this.propertyUtils.setOwlSameAsMap(entitiesEvents);
		
		this.timestampUtils = timestampUtils;
		client = new EventkgClient ();
		queries = new ArrayList<String>();
		assignedVars = new HashMap<String,String>();
		allVars = new HashMap<String,String>();
		givenVars = new HashMap<String,String>();
	}
	
	
	public int getQueryNumber() {
		return queryNumber;
	}

	
	private String generateVariable (String currentVariable, RelationSnapshot rel, String type) throws IOException
	{
		String variable = currentVariable;
		String id;
		
		int size = variable.length()-1;
		String letters = variable.substring(0, size);
		
		while (assignedVars.containsKey(variable))
		{
			int number = Character.getNumericValue(variable.charAt(size));
			number = number + 1;
			variable = letters + number;	
		}
		
		if (type.contains("relation"))
		{
			assignedVars.put(variable, rel.getRoleType());
		}
		
		if (type.contains("object"))
		{
			//" owl:sameAs " + propertyUtils.getOwlSameAs (obId,"@en") + " .\n";
			id = rel.getObject();
			final Pattern pattern = Pattern.compile("<(.+?)>");
			final Matcher m = pattern.matcher(id);
			m.find();
			id = m.group(1);
			id = "<" + id;
			id = id + ">";
			
			for (Entry <String,String> entry : assignedVars.entrySet())
			{
				if (entry.getValue().contentEquals(" owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n"))
				{
					variable = entry.getKey();
					
					candidateVariables.add(variable);
				}
			}
			assignedVars.put(variable, " owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n");
		}
		
		
		if (type.contains("subject"))
		{
			id = rel.getSubject();
			final Pattern pattern = Pattern.compile("<(.+?)>");
			final Matcher m = pattern.matcher(id);
			m.find();
			id = m.group(1);
			id = "<" + id;
			id = id + ">";
			
			for (Entry <String,String> entry : assignedVars.entrySet())
			{
				if (entry.getValue().contentEquals(" owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n"))
				{
					variable = entry.getKey();
				
					candidateVariables.add(variable);
				}
			}

			assignedVars.put(variable, " owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n");
		}
		
	
		return variable;
	}
	private void setTripleEventAttributes (String var, String id, RelationSnapshot r) throws IOException
	{
		String triple="";
		RelationSnapshot rel = r;
		String attrib = propertyUtils.getAttributes(id);
		
		
		StringTokenizer tokenAttrib = new StringTokenizer (attrib,"\n");
		
		while (tokenAttrib.hasMoreElements())
		{
			String nextAttrib = tokenAttrib.nextToken();
			if (nextAttrib.contains("nextEvent") || nextAttrib.contains("previousEvent") || nextAttrib.contains("hasPlace"))
			{
				triple = var;
				StringTokenizer tokenTriple = new StringTokenizer (nextAttrib);
				String currentTokenTriple;
				while (tokenTriple.hasMoreElements())
				{
					currentTokenTriple = tokenTriple.nextToken();
					
					if (!(currentTokenTriple.contains("entity") || currentTokenTriple.contains("event")))
						triple = triple + " " + currentTokenTriple;
					else
					{
						if (nextAttrib.contains("hasPlace"))
						{
							triple = triple + " ?place .\n";
							triple = triple + "?place owl:sameAs " + propertyUtils.getOwlSameAs (currentTokenTriple,"@en") + " .";
							break;
						}
						else
							if (nextAttrib.contains("nextEvent"))
							{
								triple = triple + " ?nextEvent .\n";
								triple = triple + "?nextEvent owl:sameAs " + propertyUtils.getOwlSameAs (currentTokenTriple,"@en") + " .";
								break;
							}
							else if (nextAttrib.contains("previousEvent"))
							{
								triple = triple + " ?previousEvent .\n";
								triple = triple + "?previousEvent owl:sameAs " + propertyUtils.getOwlSameAs (currentTokenTriple,"@en") + " .";
								break;
							}
						
						
					}
				}
				
				if(!triples.contains(triple))
					triples.add(triple);
				
			}
		}

	}
	private RelationSnapshot setObjectSubjectRelation (RelationSnapshot r) throws IOException
	{
		String rVar = "?relation1";
		String var1;
		String var2;
		RelationSnapshot updatedRelation=r;
		rVar = generateVariable (rVar,r,"relation");
		
		query = query + rVar + " rdf:type eventKG-s:Relation .\n";

		if (r.getObject().contains("<event") && r.getSubject().contains("<event"))
		{
			var1 = "?event1";
			var2 = "?event2";
			var1 = generateVariable (var1,r,"object");
			var2 = generateVariable (var2,r,"subject");
			object = var1;
			subject = var2;
			query = query + var1 + " rdf:type sem:Event .\n";
			query = query + var2 + " rdf:type sem:Event .\n";
			//nlp.setEntityNaturalLanguage(var1,r,"object");
			//nlp.setEntityNaturalLanguage(var2,r,"subject");
			
			String id = r.getObject();
			final Pattern pattern = Pattern.compile("<(.+?)>");
			final Matcher m = pattern.matcher(id);
			m.find();
			
			id = m.group(1);
			id = "<" + id;
			id = id + ">";
			
			assignedVars.put(var1, " owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n");
			givenVars.put(var1, propertyUtils.getOwlSameAs (id,"@en"));
			
			id = r.getSubject();
			
			final Matcher mSub = pattern.matcher(id);
			mSub.find();
			id = mSub.group(1);
			id = "<" + id;
			id = id + ">";
			assignedVars.put(var2, " owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n");
			givenVars.put(var2, propertyUtils.getOwlSameAs (id,"@en"));
		
		}
		else
		{
			if (r.getObject().contains("<entity") && r.getSubject().contains("<entity"))
			{
				
				var1 = "?entity1";
				var2 = "?entity2";
				var1 = generateVariable (var1,r,"object");
				var2 = generateVariable (var2,r,"subject");
				object = var1;
				subject = var2;
				query = query + rVar + " rdf:object " + object + " .\n";
				query = query + rVar + " rdf:subject " + subject + " .\n";
				query = query + rVar + " " + relationUtils.getRoleTypeWithProperty(r) + " .\n";
				relationVar = rVar;
				//nlp.setEntityNaturalLanguage(var1,r,"object");
				//nlp.setEntityNaturalLanguage(var2,r,"subject");
				
				String id = r.getObject();
				final Pattern pattern = Pattern.compile("<(.+?)>");
				final Matcher m = pattern.matcher(id);
				m.find();
				
				id = m.group(1);
				id = "<" + id;
				id = id + ">";
				givenVars.put(var1, propertyUtils.getOwlSameAs (id,"@en"));
				updatedRelation = relationUtils.addNodeRelation(var1,updatedRelation,id, "object");
				id = r.getSubject();
				
				final Matcher mSub = pattern.matcher(id);
				mSub.find();
				id = mSub.group(1);
				id = "<" + id;
				id = id + ">";
				givenVars.put(var2,propertyUtils.getOwlSameAs (id,"@en"));
				relationUtils.addNodeRelation(var1,r,id, "subject");
				updatedRelation = relationUtils.addNodeRelation(var2,updatedRelation, id,"subject");

				
			}
			
			else 
			{
				if (r.getObject().contains("<event"))	
				{	
					if (relation1==null)
					{
						var1 = "?event1";
						var1 = generateVariable (var1,r,"object");
						object = var1;
						query = query + var1 + " rdf:type sem:Event .\n";
						eventVar = var1;
						//nlp.setEntityNaturalLanguage(var1,r,"object");
				
						String id = r.getObject();
						final Pattern pattern = Pattern.compile("<(.+?)>");
						final Matcher m = pattern.matcher(id);
						m.find();
				
						id = m.group(1);
						id = "<" + id;
						id = id + ">";
				
						assignedVars.put(var1, " owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n");
						givenVars.put(var1, propertyUtils.getOwlSameAs (id,"@en"));
						setTripleEventAttributes (var1,id,r);

						relationUtils.addNodeRelation(var1,r,id, "object");
						id = r.getSubject();
						
					}
				
					else
					{
						eventVar = object = "?event1";
						eventVar = generateVariable (eventVar,r,"object");
					String id = r.getObject();
					final Pattern pattern = Pattern.compile("<(.+?)>");
					final Matcher m = pattern.matcher(id);
					m.find();
					id = m.group(1);
					id = "<" + id;
					id = id + ">";
					assignedVars.put(eventVar, " owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n");
					givenVars.put(eventVar, propertyUtils.getOwlSameAs (id,"@en"));
					setTripleEventAttributes (eventVar,id,r);

					query = query + eventVar + " rdf:type sem:Event .\n";
					//nlp.setEntityNaturalLanguage(eventVar,r,"object");
			    
				}
			  }
			if (r.getObject().contains("<entity"))
			{
				var1 = "?entity1";
				var1 = generateVariable (var1,r,"object");
				object = var1;
				entityVar2 = var1;
				//nlp.setEntityNaturalLanguage(var1,r,"object");
				
				String id = r.getObject();
				final Pattern pattern = Pattern.compile("<(.+?)>");
				final Matcher m = pattern.matcher(id);
				m.find();
				id = m.group(1);
				id = "<" + id;
				id = id + ">";
				assignedVars.put(var1, " owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n");
				givenVars.put(var1, propertyUtils.getOwlSameAs (id,"@en"));
				

			}

			if (r.getSubject().contains("<event"))
				
			{
				if (relation1==null)
				{
				var1 = "?event1";
				var1 = generateVariable (var1,r,"subject");
				subject = var1;
				query = query + var1 + " rdf:type sem:Event .\n";
				eventVar = var1;
			//	nlp.setEntityNaturalLanguage(var1,r,"subject");

				String id = r.getSubject();
				final Pattern pattern = Pattern.compile("<(.+?)>");
				final Matcher m = pattern.matcher(id);
				m.find();
				id = m.group(1);
				id = "<" + id;
				id = id + ">";
				assignedVars.put(var1, " owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n");
				givenVars.put(var1, propertyUtils.getOwlSameAs (id,"@en"));
				setTripleEventAttributes (var1,id,r);

				}
				else
				{
					eventVar = subject = "?event1";
					eventVar = generateVariable (eventVar,r,"subject");
					String id = r.getSubject();
					final Pattern pattern = Pattern.compile("<(.+?)>");
					final Matcher m = pattern.matcher(id);
					m.find();
					id = m.group(1);
					id = "<" + id;
					id = id + ">";
					assignedVars.put(eventVar, " owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n");
					query = query + eventVar + " rdf:type sem:Event .\n";
					//nlp.setEntityNaturalLanguage(eventVar,r,"subject");
					setTripleEventAttributes (eventVar,id,r);

					givenVars.put(eventVar, propertyUtils.getOwlSameAs (id,"@en"));
				}
			}
			if (r.getSubject().contains("<entity"))
			{
				var1 = "?entity1";
				var1 = generateVariable (var1,r,"subject");
				subject = var1;
				entityVar2 = var1;
			//	nlp.setEntityNaturalLanguage(var1,r,"subject");
				
				String id = r.getSubject();
				final Pattern pattern = Pattern.compile("<(.+?)>");
				final Matcher m = pattern.matcher(id);
				m.find();
				id = m.group(1);
				id = "<" + id;
				id = id + ">";
				assignedVars.put(var1, " owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n");
				givenVars.put(var1, propertyUtils.getOwlSameAs (id,"@en"));
				updatedRelation = relationUtils.addNodeRelation(var1,updatedRelation,id, "subject");
			}
		  
		}
		}
		
		query = query + rVar + " rdf:object " + object + " .\n";
		query = query + rVar + " rdf:subject " + subject + " .\n";
		
		query = query + rVar + " " + relationUtils.getRoleTypeWithProperty(r) + " .\n";
		relationVar = rVar;
		
		return updatedRelation;
	}
	
	//get a query from one relation
	public String getQueryFromRelations (RelationSnapshot r1, String obId, String subId) throws IOException, InterruptedException
	{
		
		selectClause = "SELECT DISTINCT";
		query = "{\n";
		endTimeStampValue = "";
		beginTimeStampValue = "";
		queryName = "# query number: "+ queryNumber + "\n";
		
		setObjectSubjectRelation (r1);
		
		String periodSelected = randomFilter.SelectPeriod();
		String placeSelected = randomFilter.SelectPlace();
		String EventDecisionSelected = randomFilter.SelectEventDecision();
		
		constructTimeFilter (eventAttributes);
		
		selectClause = selectClause + " "  + subject + "\n";
		//query = query + relationVar + " " + relationUtils.getRoleTypeWithProperty(r1) + " .\n";

		query = query + object + " owl:sameAs " + propertyUtils.getOwlSameAs (obId,"@en") + " .\n";
	//	query = query + "GRAPH eventKG-g:wikipedia_en {"  + object + " rdfs:label " + propertyUtils.getLabelFirstEnglishEntity(obId) + "} .\n";
		
		if (!(beginTimeStampValue.isEmpty()))
				filterTime = timestampUtils.getFilter(periodSelected, beginTimeStampPred, EventDecisionSelected, startTimeVar, endTimeVar, 
				 beginTimeStampValue, endTimeStampValue, beginYearTimeStamp, last, yearSelected, eventAttributes);

		queryWithoutTs = query;
		query = query + filterTime;
		
		queryWithoutTs = selectClause + queryWithoutTs;
		queryWithoutTs = queryWithoutTs + "}\n";
		
		selectClause =  selectClause + query;
		query = selectClause + "}\n";
		
		if (client.hasResult(query))
		{	
			queries.add(query);
		}
		else
		{
			if (client.hasResult(queryWithoutTs))
			{
				
				queries.add(queryWithoutTs);
			}
			
		}
		
		if (eventAttributes.contains("hasPlace"))
		{
			
			selectClause = "SELECT DISTINCT";
			assignedVars.clear();
			query = "{\n";
			
			queryName = "# query number: "+ queryNumber + "\n";
			setObjectSubjectRelation (r1);

			//queryName = "# ";
			
			query = query + eventVar + " sem:hasPlace " + entityVar2 + " .\n";

			if (!beginTimeStampPred.isEmpty())
				query = query + eventVar + " " + beginTimeStampPred + " " + startTimeVar + " .\n";
				
			if (!endTimeStampPred.isEmpty())
				query = query + eventVar + " " + endTimeStampPred + " " + endTimeVar + " .\n";
			
			query = query + object + " owl:sameAs " + propertyUtils.getOwlSameAs (obId,"@en") + " .\n";
			
			if (object.contains("entity"))
			{
				query = query + subject + " owl:sameAs " + propertyUtils.getOwlSameAs (subId,"@en") + " .\n";

			}
			//TimeStampUtils.setQueryName(queryName);
			selectClause = selectClause + " "  + entityVar2 + "\n";
			queryWithoutTs = query;
			query = query + filterTime;
			
			queryWithoutTs = selectClause + queryWithoutTs;
			queryWithoutTs = queryWithoutTs + "}\n";
			selectClause =  selectClause + query;
			
			query = selectClause + "}\n";
			
			if (client.hasResult(query))
			{	
				queries.add(query); 
			}
			else
			{
				
				if (client.hasResult(queryWithoutTs))
				{
					queries.add(queryWithoutTs); 
				}
			}
			
		}
		
		if (!(beginTimeStampValue.isEmpty()))
		{
			
			selectClause = "SELECT DISTINCT";
			query = "{\n";
			assignedVars.clear();

			queryName = "# query number: "+ queryNumber + "\n";
			//queryName = "# ";
			setObjectSubjectRelation (r1);

			query = query + object + " owl:sameAs " + propertyUtils.getOwlSameAs (obId,"@en") + " .\n";
			query = query + subject + " owl:sameAs " + propertyUtils.getOwlSameAs (subId,"@en") + " .\n";
			
			query = query + eventVar + " " + beginTimeStampPred + " " + startTimeVar + " .\n";
			//TimeStampUtils.setQueryName(queryName);
			selectClause = selectClause + " "  + startTimeVar + "\n";
			queryWithoutTs = query;
			//query = query + filterTime;
			
			queryWithoutTs = selectClause + queryWithoutTs;
			queryWithoutTs = queryWithoutTs + "}\n";
			
			selectClause =  selectClause + query;
			query = selectClause + "}\n";
			
		  if (client.hasResult(query))
		  {
			  queries.add(query); 
		  }
		  else
		  {
			  if (client.hasResult(queryWithoutTs))
			  {
				  queries.add(queryWithoutTs); 
			  }
			  
		  }
			 
		}
		
		if (!(endTimeStampValue.isEmpty()))
		{
			
			selectClause = "SELECT DISTINCT";
			query = "{\n";
			assignedVars.clear();

			queryName = "# query number: "+ queryNumber + "\n";
			//queryName = "# ";
			
			setObjectSubjectRelation (r1);
			
			query = query + object + " owl:sameAs " + propertyUtils.getOwlSameAs (obId,"@en") + " .\n";
			query = query + subject + " owl:sameAs " + propertyUtils.getOwlSameAs (subId,"@en") + " .\n";
			
			query = query + eventVar + " " + endTimeStampPred + " " + endTimeVar + " .\n";
			
			selectClause = selectClause + " "  + endTimeVar + "\n";
			queryWithoutTs = query;
			//query = query + filterTime;
			
			queryWithoutTs = selectClause + queryWithoutTs;
			queryWithoutTs = queryWithoutTs + "}\n";
			
			selectClause =  selectClause + query;
			query = selectClause + "}\n";
			
			if (client.hasResult(query))
			{	
				queries.add(query); 
				
			}
			else
			{
				
				if (client.hasResult(queryWithoutTs))
				{
					queries.add(queryWithoutTs); 
					
				}
			}
		}
		
		if (!queries.isEmpty())
			query = randomFilter.getRandomValue(queries);
		else
			query = "";
		
		queryNumber++;
		return query + " LIMIT 1";
		
	}
	
	private void constructTimeFilter (String eventAttributes) throws IOException
	{
		ArrayList<String> years = new ArrayList<String> ();
		
		beginYearTimeStamp = "";
		endYearTimeStamp = "";
		beginTimeStampPred = timestampUtils.getTimeStampEvent("begin", eventAttributes);
		startTimeVar = eventVar + "StartTime";
		endTimeVar = eventVar + "EndTime";
		
		if (!beginTimeStampPred.isEmpty())
		{
			StringTokenizer tokenBeginTs = new StringTokenizer (beginTimeStampPred);
			beginTimeStampPred = tokenBeginTs.nextToken();
			beginTimeStampValue = tokenBeginTs.nextToken();
			query = query + eventVar + " " + beginTimeStampPred + " " + startTimeVar + " .\n";
			beginYearTimeStamp = timestampUtils.getYearTimeStamp (beginTimeStampValue);
			assignedVars.put(startTimeVar, beginTimeStampValue);
			
		}
		
		endTimeStampPred = timestampUtils.getTimeStampEvent("end", eventAttributes);
		
		if (!endTimeStampPred.isEmpty())
		{
			StringTokenizer tokenEndTs = new StringTokenizer (endTimeStampPred);
			endTimeStampPred = tokenEndTs.nextToken();
			endTimeStampValue = tokenEndTs.nextToken();
			query = query + eventVar + " " + endTimeStampPred + " " + endTimeVar + " .\n";
			endYearTimeStamp = timestampUtils.getYearTimeStamp (endTimeStampValue);
			assignedVars.put(endTimeVar, endTimeStampValue);

		}

		if (!beginYearTimeStamp.isEmpty() && !endYearTimeStamp.isEmpty())
		{
			int begin = Integer.parseInt(beginYearTimeStamp);
			int end = Integer.parseInt(endYearTimeStamp);
			for (int i = begin;i<=end;i++)
			{
				years.add(Integer.toString(i));
			}
		}
		else
		{
			if (!beginYearTimeStamp.isEmpty())
			{
				years.add(beginYearTimeStamp);
			}
		}
		
		if (!years.isEmpty())
		{
			yearSelected = randomFilter.getRandomValue(years);
		}
		
	}
	
	private void addTimeFilterVariables (RelationSnapshot r) throws IOException
	{
		ArrayList<String> years = new ArrayList<String> ();
		String variableName;
		
		if (!r.getBeginTimestamp().isEmpty() || (relation1==null && relation2==null))
			variableName = relationVar;
		else
			variableName = eventVar;
		
		beginYearTimeStamp = "";
		endYearTimeStamp = "";
		if (!r.getBeginTimestamp().isEmpty())
			beginTimeStampPred = timestampUtils.getTimeStampEvent("begin", r.getBeginTimestamp());
		else
			beginTimeStampPred = timestampUtils.getTimeStampEvent("begin", eventAttributes);

		startTimeVar = variableName + "StartTime";
		endTimeVar = variableName + "EndTime";
		
		if (!beginTimeStampPred.isEmpty())
		{
			StringTokenizer tokenBeginTs = new StringTokenizer (beginTimeStampPred);
			beginTimeStampPred = tokenBeginTs.nextToken();
			beginTimeStampValue = tokenBeginTs.nextToken();
			query = query + variableName + " " + beginTimeStampPred + " " + startTimeVar + " .\n";
			beginYearTimeStamp = timestampUtils.getYearTimeStamp (beginTimeStampValue);
			assignedVars.put(startTimeVar, beginTimeStampValue);
			candidateVariables.add(startTimeVar);
			
		}
		
		if (!r.getBeginTimestamp().isEmpty())
			endTimeStampPred = timestampUtils.getTimeStampEvent("end", r.getEndTimestamp());
		else
			endTimeStampPred = timestampUtils.getTimeStampEvent("end", eventAttributes);
		
		if (!endTimeStampPred.isEmpty())
		{
			StringTokenizer tokenEndTs = new StringTokenizer (endTimeStampPred);
			endTimeStampPred = tokenEndTs.nextToken();
			endTimeStampValue = tokenEndTs.nextToken();
			query = query + variableName + " " + endTimeStampPred + " " + endTimeVar + " .\n";
			endYearTimeStamp = timestampUtils.getYearTimeStamp (endTimeStampValue);
			assignedVars.put(endTimeVar, endTimeStampValue);
			candidateVariables.add(endTimeVar);
		}

		if (!beginYearTimeStamp.isEmpty() && !endYearTimeStamp.isEmpty())
		{
			int begin = Integer.parseInt(beginYearTimeStamp);
			int end = Integer.parseInt(endYearTimeStamp);
			for (int i = begin;i<=end;i++)
			{
				years.add(Integer.toString(i));
			}
		}
		else
		{
			if (!beginYearTimeStamp.isEmpty())
			{
				years.add(beginYearTimeStamp);
			}
		}
		
		if (!years.isEmpty())
		{
			yearSelected = randomFilter.getRandomValue(years);
		}
		
	}
	
	private void constructTimeFilter (String periodSelected, String EventDecisionSelected, ArrayList<String> variables, String decision)
	{
		TimeFilterSnapshot	timeFilterSnapshot;
		String newFilterTime = "";
		
		if (!(beginTimeStampValue.isEmpty()))
			filterTime = timestampUtils.getFilter(periodSelected, beginTimeStampPred, EventDecisionSelected, startTimeVar, endTimeVar, 
			 beginTimeStampValue, endTimeStampValue, beginYearTimeStamp, last, yearSelected, eventAttributes);
	
		findVarFilter (startTimeVar,variables);
		findVarFilter (endTimeVar,variables);
		
		if (!filterTime.isEmpty())
		{
				timeFilterSnapshot = new TimeFilterSnapshot (periodSelected, beginTimeStampValue, EventDecisionSelected, endTimeStampValue, 
				beginYearTimeStamp, yearSelected, last);
				//nlp.buildTimeFilterExpression(timeFilterSnapshot);
				//nlp.setTimeFilterExpression(timestampUtils.getQueryName());
				
				
				if (queryType.contains("ask"))
				{
						
					StringTokenizer tokenFilter = new StringTokenizer (filterTime,"\n");
					while (tokenFilter.hasMoreTokens())
					{
						String currentLineFilter = tokenFilter.nextToken();
					
						if (filterTime.contains("StartTime") && filterTime.contains("EndTime"))
						{
							if (currentLineFilter.contains(decision))
								newFilterTime =  newFilterTime + currentLineFilter + "\n";
						}
						else
							newFilterTime =  newFilterTime + currentLineFilter + "\n";
					}
					filterTime = newFilterTime;
				}
		}
		else
			nlp.setTimeFilterExpression("");
	}
	
	private void findVarFilter (String var, ArrayList<String> variables)
	{
		Pattern pattern = Pattern.compile("(.+?)"+var+"(.+?)");
		Matcher m = pattern.matcher(filterTime);
		
		for (int i=0;i<variables.size();i++)
		{
		  
			if (variables.get(i).contentEquals(var))
			{
				try {
					m.find();
					String rs = m.group(1);
					if (rs!=null)
						filterTime = "";
				 } catch (Exception e)
				 {
					 return;
				 }
			}
		 
		}
		
	}
	
	public String getNaturalLanguage ()
	{
		return nlp.getNaturalLanguage();
	}
	
	//get a query from 2 relations
	public String getQueryFromRelations (RelationSnapshot r1, RelationSnapshot r2) throws IOException, InterruptedException
	{
		String varSecondEntity = "";
		String queryWithoutFilterTime;
		RelationSnapshot rel1 = r1;
		RelationSnapshot rel2 = r2;
		
		nlp = new NlpConversion (typesEntities, propertyUtils);
		variables.clear();
		query = "";
		
		if (queryType.contains("ask"))
			selectClause = "ASK \n";
		
		if (queryType.contains("select"))
			selectClause = "SELECT DISTINCT";
		
		if (queryType.contains("count"))
			selectClause = "SELECT";

		nlp.setQuestionType(queryType);
		
		assignedVars.clear();

		String periodSelected = randomFilter.SelectPeriod();
		String EventDecisionSelected = randomFilter.SelectEventDecision();
		
		queryName = "";
			
		if (r2 == null)
			return "";
		
		ArrayList<String> startEndDecision = new ArrayList<String> ();
		startEndDecision.add("StartTime");
		startEndDecision.add("EndTime");
		String timDecision = randomFilter.getRandomValue(startEndDecision);
	
		rel1 = setObjectSubjectRelation (r1);
		rel2 = setObjectSubjectRelation (r2);

		addTimeFilterVariables (r1);
		
		allVars = new HashMap<String,String>(assignedVars); 
		
		if (relation1==null)
			varSecondEntity = getVarSecondEntity ("?entity1");
		/*else
			varSecondEntity = getVarSecondEntity ("?event1");*/
		
		if (!queryType.contains("ask"))
			variables = selectVariables ();
		
		//nlp.setVariables(variables);
			
		if (relation1==null)
		{	
			if (!(assignedVars.containsKey(varSecondEntity) && assignedVars.containsKey("?entity1")))
			{
				query = query + "?entity1 owl:sameAs ?entity1Id .\n";
				query = query + varSecondEntity + " owl:sameAs ?entity1Id .\n";
			}
		}
	/*	else
		{
			if (!(assignedVars.containsKey(varSecondEntity) && assignedVars.containsKey("?event1")))
			{
				query = query + "?event1 owl:sameAs ?event1Id .\n";
				query = query + varSecondEntity + " owl:sameAs ?event1Id .\n";
			}
		}
		*/
		
		if (queryType.contains("select"))
			for (int i=0;i<variables.size();i++)
				selectClause = selectClause + " " + variables.get(i);
		
		if (queryType.contains("select") && !selectClause.contains("DISTINCT"))
		{
			StringTokenizer tokenClause = new StringTokenizer  (selectClause);
			String newSelect = tokenClause.nextToken() + " DISTINCT ";
			newSelect = newSelect  + tokenClause.nextToken();
			selectClause = newSelect;
		}	
			
			for (Entry<String,String> entry : assignedVars.entrySet())
			{
				/*if (entry.getValue().contains("wikidata"))
					return "";
				*/
				String currentKey = entry.getKey();
			
				if (currentKey.contains("relation"))
					continue;
			
					if (!currentKey.contains("StartTime") && !(currentKey.contains("EndTime")))
						query = query + entry.getKey() + entry.getValue();
			}
		
		removeVariablesFromGivenVars (variables);

		//nlp.setGivenVars(givenVars);
		
		if (queryType.contains("ask"))
		{
			selectClause = selectClause + "{\n" + query + "}";
			query = selectClause;
		}
		
		if (queryType.contains("select"))
		{
			selectClause = selectClause + " WHERE {\n" + query;
			query = selectClause;
		}
		
		if (queryType.contains("count"))
		{
			
			String selectedVariable = "";
			
			if (!variables.isEmpty())
			{
				for (int i=0;i<variables.size();i++)
				if (!variables.get(i).contains("StartTime") && !(variables.get(i).contains("EndTime")))
				{
					selectedVariable = variables.get(i);
					break;
				}
			}
			
			if (selectedVariable.isEmpty())
			{
				
				queryType="select";
			//	nlp.setQuestionType(queryType);
				selectClause = "SELECT DISTINCT ";
				
				if (variables.isEmpty())
					return "";
				else
				{
					selectClause = selectClause + variables.get(0) + " WHERE {\n" + query;
					query = selectClause;
				}
			}
			
			else
			{
			//SELECT ?event1 ( count (distinct ?event1 ) as ?count)	
			selectClause = selectClause + " count ( DISTINCT " +  selectedVariable + " ) as ?count" + " WHERE {\n" + query;
			nlp.setCountVar(selectedVariable);
			}
			query = selectClause;
		}
		
		String varTimeStamp = "";
		 ArrayList<String> tsVars = new ArrayList<String>();
		 String owlSameAs = "";
		 
		 if(!variables.isEmpty() && variables.contains("?event"))
		 {
			
			if (!variables.get(0).contains("StartTime") && !(variables.get(0).contains("EndTime")))
			{
				for (Entry<String,String> entryAssigned : assignedVars.entrySet())
				{
					if (entryAssigned.getKey().contains("StartTime"))
					{
						varTimeStamp = entryAssigned.getKey().replaceAll("StartTime","");
						
						 owlSameAs = assignedVars.get(varTimeStamp);
						tsVars.add(entryAssigned.getKey());
						
						
					}
					
					if (entryAssigned.getKey().contains("EndTime"))
					{
						varTimeStamp = entryAssigned.getKey().replaceAll("EndTime","");
						
						 owlSameAs = assignedVars.get(varTimeStamp);
						
						tsVars.add(entryAssigned.getKey());
						
					}
					
				}
				
				if (!tsVars.isEmpty())
				{
					if (!timestampUtils.isYearString(owlSameAs))
					{
						constructTimeFilter (periodSelected,EventDecisionSelected,variables,timDecision);
						
					}
				}
			}
		}
		 else
		 {
			 tsVars = new ArrayList<String>();
					for (Entry<String,String> entryAssigned : assignedVars.entrySet())
					{
						if (entryAssigned.getKey().contains("StartTime"))
						{
							varTimeStamp = entryAssigned.getKey().replaceAll("StartTime","");
							
							owlSameAs = assignedVars.get(varTimeStamp);
							tsVars.add(entryAssigned.getKey());
							
							
						}
						
						if (entryAssigned.getKey().contains("EndTime"))
						{
							varTimeStamp = entryAssigned.getKey().replaceAll("EndTime","");
							
							 owlSameAs = assignedVars.get(varTimeStamp);
							
							tsVars.add(entryAssigned.getKey());
							
						}
						
					}
					
					if (!tsVars.isEmpty())
					{
						if  (owlSameAs!=null)
						if (!timestampUtils.isYearString(owlSameAs))
						{
							constructTimeFilter (periodSelected,EventDecisionSelected,variables,timDecision);
							
						}
					}
				
		 }
			
		 
		 
		testQuery ();
		
		if (relation1==null)
		{
			periodSelected = randomFilter.SelectPeriod();
			EventDecisionSelected = randomFilter.SelectEventDecision();
		
			addTimeFilterVariables (r2);
			constructTimeFilter (periodSelected,EventDecisionSelected,variables,timDecision);
			testQuery ();
		}
		
		if (!query.isEmpty() && queryType.contains("select"))
			query = query + "\n} LIMIT 1\n";
		
		if (!query.isEmpty() && queryType.contains("count"))
			query = query + "\n}\nLIMIT 1";
		
	/*	if (!query.isEmpty() && queryType.contains("ask"))
			query = query + "\n}";
		
		*/
		removeTimeStampInconsistencies ();
		return query;
		
	}
	
	private void removeTimeStampInconsistencies ()
	{
		StringTokenizer tokenQuery = new StringTokenizer (query,"\n");
		String newQuery = "";
		String beginTs = "";
		String endTs = "";
		String filterBegin = "";
		String filterEnd = "";
		String select = "";
		String var = "";
		HashSet<String> nonVars = new HashSet<String>();
		int totalNonVars = 0;
		int totalOwlSameAs = 0;
		boolean hasSameAs = false;
		while (tokenQuery.hasMoreTokens())
		{
			String currentTokenQueryLine = tokenQuery.nextToken();
			
			
			if ((currentTokenQueryLine.contains("SELECT")))
			{
				select = currentTokenQueryLine;
				
				StringTokenizer tokenVar = new StringTokenizer (currentTokenQueryLine);
				while (tokenVar.hasMoreElements())
				{
					String currentToken = tokenVar.nextToken();
					if (currentToken.contains("?entity") || currentToken.contains("?event"))
					{
						var = currentToken;
						break;
					}
				}
			}
			
		
			if (currentTokenQueryLine.contains("sem:hasBeginTimeStamp"))
				beginTs = currentTokenQueryLine;
			
			if (currentTokenQueryLine.contains("sem:hasEndTimeStamp"))
				endTs = currentTokenQueryLine;
		
			if (currentTokenQueryLine.contains("FILTER") && currentTokenQueryLine.contains("StartTime"))
			{
				filterBegin = currentTokenQueryLine;
			}
			
			if (currentTokenQueryLine.contains("FILTER") && currentTokenQueryLine.contains("EndTime"))
			{
				filterEnd = currentTokenQueryLine;
			}
			
		}
		
		if (!beginTs.isEmpty() && filterBegin.isEmpty() && (!(select.contains("StartTime"))))
		{
			tokenQuery = new StringTokenizer (query,"\n");
			
			while (tokenQuery.hasMoreTokens())
			{
				
				String currentTokenQueryLine = tokenQuery.nextToken();
				if (!currentTokenQueryLine.contentEquals(beginTs))
					newQuery = newQuery + currentTokenQueryLine + "\n";
			}
			
			query = newQuery;
		}
		
		if (!endTs.isEmpty() && filterEnd.isEmpty() && (!(select.contains("EndTime"))))
		{
			newQuery = "";
			tokenQuery = new StringTokenizer (query,"\n");
			
			while (tokenQuery.hasMoreTokens())
			{
				String currentTokenQueryLine = tokenQuery.nextToken();
				if (!currentTokenQueryLine.contentEquals(endTs))
					newQuery = newQuery + currentTokenQueryLine + "\n";
			}
			query = newQuery;
		}
		
		tokenQuery = new StringTokenizer (query,"\n");
		
		while (tokenQuery.hasMoreElements())
		{
			String currentTokenQueryLine = tokenQuery.nextToken();
			
			if (!currentTokenQueryLine.contains("SELECT"))
			{
				StringTokenizer tokenWrongVar = new StringTokenizer (currentTokenQueryLine);
				while (tokenWrongVar.hasMoreElements())
				{
					String currentToken = tokenWrongVar.nextToken();
					if (currentToken.contains("?entity") || currentToken.contains("?event"))
					{
						if (currentToken.contains("StartTime") || currentToken.contains("EndTime"))
							continue;
						currentToken = currentToken.replaceAll("\\(", "");
					
						if (!currentToken.contentEquals(var))
						{
							nonVars.add(currentToken);
							if (currentTokenQueryLine.contains("owl:sameAs"))
							{
								totalOwlSameAs++;
								break;
							}
						}
						
					}
				}
				
				
			}
			
			
		}
		
		if (nonVars.size()!=totalOwlSameAs && !queryType.contains("ask"))
		{
			query = "";
			return;
		}
		
	}
	private void removeVariablesFromGivenVars (ArrayList<String> variables)
	
	{
		for (int i=0;i<variables.size();i++)
		{
			if (givenVars.containsKey(variables.get(i)))
					givenVars.remove(variables.get(i));
		}
	}
	//get a query from 2 relations
	public String getQueryFromRelations (RelationSnapshot r1, RelationSnapshot r2, String eventAttributes) throws IOException, InterruptedException
	{
		String varSecondEntity = "";
		String queryWithoutFilterTime;
		ArrayList<String> queryTypes = new ArrayList<String>();
		queryTypes.add("ask");
		queryTypes.add("select");
		queryTypes.add("count");

		queryType = randomFilter.getRandomValue(queryTypes);
		
		ArrayList<String> variables = new ArrayList<String>();
		
		if (queryType.contains("ask"))
			selectClause = "ASK \n";
		else
			selectClause = "SELECT DISTINCT";
		query = "{\n";
		assignedVars.clear();
		String periodSelected = randomFilter.SelectPeriod();
		String EventDecisionSelected = randomFilter.SelectEventDecision();
		
		queryName = "# query number: "+ queryNumber + "\n";
			
		if (r2 == null)
			return "";
		
		//System.out.println(propertyUtils.getOwlSameAs (r2.getObject(), "@en"));
		//System.out.println(propertyUtils.getOwlSameAs (r2.getSubject(), "@en"));
		
		setObjectSubjectRelation (r1);
		setObjectSubjectRelation (r2);
		
		StringTokenizer token = new StringTokenizer (r1.getObject());
		String obj = token.nextToken();
		obj = token.nextToken();
		
		token = new StringTokenizer (r1.getSubject());
		String subj = token.nextToken();
		subj = token.nextToken();
		//System.out.println(propertyUtils.getOwlSameAs (obj, "@en"));
		//System.out.println(propertyUtils.getOwlSameAs (subj, "@en"));
		
		queryNumber++;
		
		addTimeFilterVariables (r1);
		allVars = assignedVars; 
		varSecondEntity = getVarSecondEntity ("?event1");
		variables = selectVariables ();
		
		constructTimeFilter (periodSelected,EventDecisionSelected,variables,"");
		
		if (!(assignedVars.containsKey(varSecondEntity) && assignedVars.containsKey("?entity1")))
		{
			query = query + "?entity1 owl:sameAs ?entity1Id .\n";
			query = query + varSecondEntity + " owl:sameAs ?entity1Id .\n";
		}
		for (int i=0;i<variables.size();i++)
			selectClause = selectClause + " " + variables.get(i);
		
		for (Entry<String,String> entry : assignedVars.entrySet())
		{
			String currentKey = entry.getKey();
			
			if (!currentKey.contains("StartTime") && !(currentKey.contains("EndTime")))
				query = query + entry.getKey() + entry.getValue();
		}
		
		if (queryType.contains("select"))
		{
			selectClause = selectClause + " WHERE \n" + query;
			query = selectClause;
		}
		else
		{
			selectClause = selectClause + "{\n" + query + "}";
			query = selectClause;
		}
		
		
		testQuery ();
		
			periodSelected = randomFilter.SelectPeriod();
			EventDecisionSelected = randomFilter.SelectEventDecision();
		
			addTimeFilterVariables (r2);
			constructTimeFilter (periodSelected,EventDecisionSelected,variables,"");
			testQuery ();
		
			if (!query.isEmpty() && queryType.contains("select"))
				query = query + "\n} LIMIT 1\n";
			
			if (!query.isEmpty() && queryType.contains("count"))
				query = query + "\n} \nORDER BY DESC(?count)";
			
			if (!query.isEmpty() && queryType.contains("ask"))
				return query;
			
			return query;
		
	}
	
	private void testQuery () throws InterruptedException
	{
	
		boolean isCountVarFound = false;
		
		if (queryType.contains("select"))
		{
			queryWithoutTs = query + "\n} LIMIT 1";
			query = query + filterTime + "\n} LIMIT 1";
		
			if (client.hasResult(query))
			{	
				query = query.replaceAll("\n} LIMIT 1", "");
				filterTime = "";
				return;
			}
				else
				{
					
					if (client.hasResult(queryWithoutTs))
					{
						query = queryWithoutTs;
						query = query.replaceAll("\n} LIMIT 1", "");
						filterTime = "";
						nlp.setTimeFilterExpression("");
						return;
					}
					else
						query = "";
			
				}
		
			if (!query.isEmpty())
				query = query.replaceAll("\n} LIMIT 1", "");
			filterTime = "";
		}
		
		if (queryType.contains("count"))
		{
			queryWithoutTs = query + "\n} LIMIT 1";
			//query = queryWithoutTs;
			query = query + filterTime + "\n} LIMIT 1";
			
			if (client.hasResult(query))
			{	
				
				if (!(client.getValue()> 1))
				{
					/*
					isCountVarFound = findCountVariable ();
					
					if (isCountVarFound)
					{
						query = query.replaceAll("\n} LIMIT 1", "");
						filterTime = "";
						return;
					}
					else {
						if (client.hasResult(queryWithoutTs))
						{
							query = queryWithoutTs;
							
							if (!(client.getValue()> 1))
							{
								isCountVarFound = findCountVariable ();

								if (isCountVarFound)
								{
									query = query.replaceAll("\n} LIMIT 1", "");
									filterTime = "";
									return;
								}
								else {
									changeFromCountToSelect();
									
									if (client.hasResult(query))
									{ 
										query = query.replaceAll("\n} LIMIT 1", "");
										filterTime = "";
										return;
										
									} else {
									query = "";
									return ;
									}
								}
							}
							else
							{
								query = query.replaceAll("\n} LIMIT 1", "");
								filterTime = "";
								return;
							}
							
						}
					}*/
					query = "";
					return;
				}
				else
				{
					query = query.replaceAll("\n} LIMIT 1", "");
					filterTime = "";
					return;
				}
			
			}
				
			else
				{
					
					if (client.hasResult(queryWithoutTs))
					{
						query = queryWithoutTs;
						
						if (!(client.getValue()> 1))
						{
							/*
							isCountVarFound = findCountVariable ();

							if (isCountVarFound)
							{
								query = query.replaceAll("\n} LIMIT 1", "");
								filterTime = "";
								return;
							}
							else {
								query = "";
								return ;
							}*/
							query = "";
							return;
						}
						else
						{
							query = query.replaceAll("\n} LIMIT 1", "");
							filterTime = "";
							return;
						}
						
					}
					else
					{
						query = "";
						return;
					}
				}
		}
		
		if (queryType.contains("ask"))
			if (client.hasResult(query))
			{	
				return;
			}
			else
				query = "";
	}
	
	private boolean findCountVariable () throws InterruptedException
	{
		String secondVar="";
		String selectClause = "SELECT count ( DISTINCT ";
		String newQuery="";
		String originalQuery = query;
		for (Entry<String,String> entry : assignedVars.entrySet())
		{
			if (entry.getKey().contains("?entity") || (entry.getKey().contains("?event") && !(entry.getKey().contains("Time"))))
			{
				secondVar = entry.getKey();
				
				selectClause = selectClause + secondVar + " ) as ?count WHERE {\n";
				
				StringTokenizer tokenQuery = new StringTokenizer (query,"\n");
				
				while (tokenQuery.hasMoreElements())
				{
					String line = tokenQuery.nextToken();
					
					if (!line.contains("SELECT") && ! (line.contains(secondVar + " owl:sameAs" )))
						newQuery = newQuery + line + "\n";
				}
				newQuery = newQuery.replaceAll("\n} LIMIT 1", "");
				newQuery = newQuery + variables.get(0) + allVars.get(variables.get(0));
				newQuery = newQuery + "\n} LIMIT 1";
				
				query = selectClause + newQuery;
				
				if (client.hasResult(query))
				{
					if (!(client.getValue()> 1))
					{
						newQuery = "";
						selectClause = "SELECT count ( DISTINCT ";
						query = originalQuery;
					}
					else
					{
						assignedVars.remove(secondVar);
						return true;
					}
				}
			}
		}
		
		return false;
		
		
	}
	private void changeFromCountToSelect ()
	{
		String newQuery = "";
		StringTokenizer tokenQuery = new StringTokenizer (query,"\n");
		String select = "SELECT DISTINCT " + variables.get(0) + " WHERE {\n";
		
		while (tokenQuery.hasMoreElements())
		{
			String line = tokenQuery.nextToken();
			if (!line.contains("SELECT"))
				newQuery = newQuery + line + "\n";
			
		}
		
		newQuery = select + newQuery;
		query = newQuery;
	}
	private String getVarSecondEntity (String varEntity)
	{
		String valueVarEntity = "";
		String var = "";
		
		for (Entry<String,String> entry : allVars.entrySet())
		{
			if (entry.getKey().contentEquals(varEntity))
			{
				valueVarEntity = entry.getValue();
				break;
			}
		}
		
		for (Entry<String,String> entry : allVars.entrySet())
		{
			if (entry.getValue().contentEquals(valueVarEntity) && !(entry.getKey().contentEquals(varEntity)))
			{
				var = entry.getKey();
				break;
			}
		}
		
		return var;
	}
	private ArrayList<String> selectVariables ()
	{
		String keySelected;
		String value;
		ArrayList<String> vars = new ArrayList<String> ();
		ArrayList<String> values = new ArrayList<String>();
		ArrayList<String> relationKeys = new ArrayList<String>();
		int maxVar = 1;
		int var=0;
		Collection<String> keys = assignedVars.keySet();
		
		for (Entry<String,String> entry : assignedVars.entrySet())
			if (entry.getKey().contains("?relation") && !(entry.getKey().contains("Time")))
				relationKeys.add(entry.getKey());
			
		for (int i = 0 ; i<relationKeys.size(); i++)
		{
			assignedVars.remove(relationKeys.get(i));
			keys.remove(relationKeys.get(i));
		}
		
		int entities = 0;
		int events = 0;
		
		
		for (int i = 0;i<candidateVariables.size();i++ )
		{
			if (candidateVariables.get(i).contains("?entity"))
				entities ++;
			
			if (candidateVariables.get(i).contains("?event") && !(candidateVariables.get(i).contains("Time")))
				events ++;
		}
		
		ArrayList<String> newCandidates = new ArrayList<String>();
		
	//	if ((entities>0 || queryType.contains("count")))
		if (events == 0 || queryType.contains("count"))
		{
			for (int i = 0;i<candidateVariables.size();i++ )
			{
				if (!candidateVariables.get(i).contains("Time"))
					newCandidates.add(candidateVariables.get(i));
			}
			candidateVariables = newCandidates;
			
		}
		
		while (var<maxVar)
		{
		  try {	
		//	keys = assignedVars.keySet();
			keys = candidateVariables;
			keySelected = randomFilter.getRandomValue(keys);
			value = assignedVars.get(keySelected);
			
			String varTimeStamp = "";
			
			if (keySelected.contains("StartTime"))
			{
				varTimeStamp = keySelected.replaceAll("StartTime","");
				String owlSameAs = assignedVars.get(varTimeStamp);
				
				if (timestampUtils.isYearString(owlSameAs))
				{
					queryType  = "select";
					continue;
				}
					
			}
			else if (keySelected.contains("EndTime"))
			{
				varTimeStamp = keySelected.replaceAll("EndTime","");
				String owlSameAs = assignedVars.get(varTimeStamp);
				
				if (timestampUtils.isYearString(owlSameAs))
					continue;
			}
			
			if (!vars.isEmpty())
				while (hasValue (value,values))
				{
					keySelected = randomFilter.getRandomValue(keys);
					value = assignedVars.get(keySelected);
				}
			values.add(value);
			assignedVars.remove(keySelected);
			removeRepeatedVariablebyValue (keySelected,value);
			vars.add(keySelected);
			var++;
		  } catch (Exception e )
		  {
			  
			  return vars;
		  }
		}
		
		return vars;
	}
	
	private boolean hasValue (String currentValue, ArrayList<String> values)
	{
		if (values.contains(currentValue))
			return true;
		return false;	
	}
	
	private void removeRepeatedVariablebyValue (String currentKey,String currentValue)
	{
		String key = null;
		
		for (Entry<String,String> entry : assignedVars.entrySet())
			if (entry.getValue().contentEquals(currentValue) && !(entry.getKey().contentEquals(currentKey)))
				key = entry.getKey();
		
		if (key!=null)
			assignedVars.remove(key);
	}
}
