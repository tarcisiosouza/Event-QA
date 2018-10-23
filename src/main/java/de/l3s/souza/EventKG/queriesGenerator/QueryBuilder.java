package de.l3s.souza.EventKG.queriesGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.l3s.elasticquery.RelationSnapshot;

public class QueryBuilder {

	private String queryType;
	private String eventAttributes;
	private String predicateNlpRelation1;
	private String predicateNlpRelation2;
	private String labelObjectRelation1;
	private String labelSubjectRelation1;
	private String labelObjectRelation2;
	private String labelSubjectRelation2;
	private String timeFilterNlp;
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
	private HashMap<String,String> allVars;

	
	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public QueryBuilder(RelationUtils relationUtils, RelationSnapshot relation, PropertyUtils propertyUtils, 
			TimeStampUtils timestampUtils, String eventAttributes) {
		
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
		this.relation = relation;
		client = new EventkgClient ();
		queries = new ArrayList<String>();
		assignedVars = new HashMap<String,String>();
		allVars = new HashMap<String,String>();

		
	}

	
	public QueryBuilder(RelationUtils relationUtils, RelationSnapshot relation1, RelationSnapshot relation2, PropertyUtils propertyUtils, 
			TimeStampUtils timestampUtils, String eventAttributes) {
		
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
		this.propertyUtils = propertyUtils;
		this.timestampUtils = timestampUtils;
		client = new EventkgClient ();
		queries = new ArrayList<String>();
		assignedVars = new HashMap<String,String>();
		allVars = new HashMap<String,String>();

		
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
			assignedVars.put(variable, " owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n");
		}
		
		if (assignedVars.isEmpty())
			System.out.println();
		return variable;
	}
	
	private void setObjectSubjectRelation (RelationSnapshot r) throws IOException
	{
		String rVar = "?relation1";
		String var1;
		String var2;
		
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
				return;
			}
			
			if (r.getObject().contains("<event"))
			{
				if (relation1==null)
				{
				var1 = "?event1";
				var1 = generateVariable (var1,r,"object");
				object = var1;
				query = query + var1 + " rdf:type sem:Event .\n";
				eventVar = var1;
				}
				else
				{
					eventVar = object = "?event1";
					String id = r.getObject();
					final Pattern pattern = Pattern.compile("<(.+?)>");
					final Matcher m = pattern.matcher(id);
					m.find();
					id = m.group(1);
					id = "<" + id;
					id = id + ">";
					assignedVars.put(eventVar, " owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n");
					query = query + eventVar + " rdf:type sem:Event .\n";
				}
			}
			else
			{
				var1 = "?entity1";
				var1 = generateVariable (var1,r,"object");
				object = var1;
				entityVar2 = var1;
			
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
				}
				else
				{
					eventVar = subject = "?event1";
					String id = r.getSubject();
					final Pattern pattern = Pattern.compile("<(.+?)>");
					final Matcher m = pattern.matcher(id);
					m.find();
					id = m.group(1);
					id = "<" + id;
					id = id + ">";
					assignedVars.put(eventVar, " owl:sameAs " + propertyUtils.getOwlSameAs (id,"@en") + " .\n");
					query = query + eventVar + " rdf:type sem:Event .\n";
				}
			}
			else
			{
				var1 = "?entity1";
				var1 = generateVariable (var1,r,"subject");
				subject = var1;
				entityVar2 = var1;
			}
		}
		
		query = query + rVar + " rdf:object " + object + " .\n";
		query = query + rVar + " rdf:subject " + subject + " .\n";
		
		query = query + rVar + " " + relationUtils.getRoleTypeWithProperty(r) + " .\n";
		relationVar = rVar;
		
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
	
	private void constructTimeFilter (String periodSelected, String EventDecisionSelected, ArrayList<String> variables)
	{
		
	  
		if (!(beginTimeStampValue.isEmpty()))
			filterTime = timestampUtils.getFilter(periodSelected, beginTimeStampPred, EventDecisionSelected, startTimeVar, endTimeVar, 
			 beginTimeStampValue, endTimeStampValue, beginYearTimeStamp, last, yearSelected, eventAttributes);
		
		findVarFilter (startTimeVar,variables);
		findVarFilter (endTimeVar,variables);
	 
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
	//get a query from 2 relations
	public String getQueryFromRelations (RelationSnapshot r1, RelationSnapshot r2) throws IOException, InterruptedException
	{
		String varSecondEntity = "";
		String queryWithoutFilterTime;
		ArrayList<String> variables = new ArrayList<String>();
		ArrayList<String> queryTypes = new ArrayList<String>();
		queryTypes.add("ask");
		queryTypes.add("select");
		queryTypes.add("count");
		queryType = randomFilter.getRandomValue(queryTypes);
		
		queryType = "select";
		
		if (queryType.contains("ask"))
			selectClause = "ASK \n";
		
		if (queryType.contains("select"))
			selectClause = "SELECT DISTINCT";
		
		if (queryType.contains("count"))
			selectClause = "SELECT";

		assignedVars.clear();

		String periodSelected = randomFilter.SelectPeriod();
		String EventDecisionSelected = randomFilter.SelectEventDecision();
		
		queryName = "";
			
		if (r2 == null)
			return "";
		
		/*
		 * private String predicateNlpRelation1;
		private String predicateNlpRelation2;
		private String labelObjectRelation1;
		private String labelSubjectRelation1;
		private String labelObjectRelation2;
		private String labelSubjectRelation2;
		 * */
		
		setObjectSubjectRelation (r1);
		setObjectSubjectRelation (r2);
		
		//assignNlpVariables (r1,r2);
		
		addTimeFilterVariables (r1);
		allVars = assignedVars; 
		if (relation1==null)
			varSecondEntity = getVarSecondEntity ("?entity1");
		/*else
			varSecondEntity = getVarSecondEntity ("?event1");*/
		variables = selectVariables ();
		
		constructTimeFilter (periodSelected,EventDecisionSelected,variables);
		
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
		
		if (queryType.contains("select") || queryType.contains("count"))
			for (int i=0;i<variables.size();i++)
				selectClause = selectClause + " " + variables.get(i);
		
		for (Entry<String,String> entry : assignedVars.entrySet())
		{
			String currentKey = entry.getKey();
			
			if (!currentKey.contains("StartTime") && !(currentKey.contains("EndTime")))
				query = query + entry.getKey() + entry.getValue();
		}
		
		if (queryType.contains("ask"))
		{
			selectClause = selectClause + "{\n" + query + "}";
			query = selectClause;
		}
		
		if (queryType.contains("count"))
		{
			
			String selectedVariable = "";
			for (int i=0;i<variables.size();i++)
				if (!variables.get(i).contains("StartTime") && !(variables.get(i).contains("EndTime")))
				{
					selectedVariable = variables.get(i);
					break;
				}
			
			if (selectedVariable.isEmpty())
			{
				queryType="select";
				
				selectClause = selectClause + " WHERE {\n" + query;
				query = selectClause;
			}
			else
			{
			selectClause = selectClause + " (count(" + "" + selectedVariable + ") as ?count)" + " WHERE {\n" + query;
			}
			query = selectClause;
		}
		
		if (queryType.contains("select"))
		{
			selectClause = selectClause + " WHERE {\n" + query;
			query = selectClause;
		}
		
		testQuery ();
		
		if (relation1==null)
		{
			periodSelected = randomFilter.SelectPeriod();
			EventDecisionSelected = randomFilter.SelectEventDecision();
		
			addTimeFilterVariables (r2);
			constructTimeFilter (periodSelected,EventDecisionSelected,variables);
			testQuery ();
		}
		
		if (!query.isEmpty() && queryType.contains("select"))
			query = query + "\n} LIMIT 1\n";
		
		if (!query.isEmpty() && queryType.contains("count"))
			query = query + "\n} LIMIT 1\n ORDER BY DESC(?count)";
		
		if (!query.isEmpty() && queryType.contains("ask"))
			query = query + "\n}";
		return query;
		
	}
	
	private void assignNlpVariables (RelationSnapshot r1, RelationSnapshot r2) throws IOException
	{
		String r1ObjectId = getIdElementRelation (r1, "object");
		String r1SubjectId = getIdElementRelation (r1, "subject");
		String r2ObjectId = getIdElementRelation (r2, "object");
		String r2SubjectId = getIdElementRelation (r2, "subject");
		
		if (r1.getRoleType().contains("wdt"))
		{
			predicateNlpRelation1 = propertyUtils.getLabelFirstEnglishProperty(r1.getRoleType());
		}
		else
		{
			StringTokenizer token = new StringTokenizer (r1.getRoleType());
			predicateNlpRelation1 = token.nextToken();
			predicateNlpRelation1 = token.nextToken();
		}
		if (r2.getRoleType().contains("wdt"))
		{
			predicateNlpRelation2 = propertyUtils.getLabelFirstEnglishProperty(r2.getRoleType());
		}
		else
		{
			StringTokenizer token = new StringTokenizer (r2.getRoleType());
			predicateNlpRelation2 = token.nextToken();
			predicateNlpRelation2 = token.nextToken();
		}
	}
	private String getIdElementRelation (RelationSnapshot r, String type)
	{
		
		String value;
		
		if (type.contains("object"))
			value = r.getObject();
		else
			value = r.getSubject();
		StringTokenizer token = new StringTokenizer (value);
		String element = token.nextToken();
		element = token.nextToken();
		
		return element;
	}
	
	//get a query from 2 relations
	public String getQueryFromRelations (RelationSnapshot r1, RelationSnapshot r2, String eventAttributes) throws IOException, InterruptedException
	{
		String varSecondEntity = "";
		String queryWithoutFilterTime;
		ArrayList<String> queryTypes = new ArrayList<String>();
		queryTypes.add("ask");
		queryTypes.add("select");
		queryType = randomFilter.getRandomValue(queryTypes);
		
		queryType = "count";
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
		
		constructTimeFilter (periodSelected,EventDecisionSelected,variables);
		
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
			constructTimeFilter (periodSelected,EventDecisionSelected,variables);
			testQuery ();
		
			if (!query.isEmpty() && queryType.contains("select"))
				query = query + "\n} LIMIT 1\n";
			
			if (!query.isEmpty() && queryType.contains("count"))
				query = query + "\n} LIMIT 1\n ORDER BY DESC(?count)";
			
			if (!query.isEmpty() && queryType.contains("ask"))
				query = query + "\n}";
			return query;
		
	}
	
	private void testQuery () throws InterruptedException
	{
		
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
			queryWithoutTs = query + "\n} LIMIT 1 \n ORDER BY DESC(?count)";
			query = query + filterTime + "\n} LIMIT 1 \n ORDER BY DESC(?count) ";
		
			if (client.hasResult(query))
			{	
				query = query.replaceAll("\n} LIMIT 1 \n ORDER BY DESC(?count)", "");
				filterTime = "";
				return;
			}
				else
				{
					
					if (client.hasResult(queryWithoutTs))
					{
						query = queryWithoutTs;
						query = query.replaceAll("\n} LIMIT 1 \n ORDER BY DESC(?count)", "");
						filterTime = "";
						return;
					}
					else
						query = "";
			
				}
		
			if (!query.isEmpty())
				query = query.replaceAll("\n} LIMIT 1 \n ORDER BY DESC(?count)", "");
			filterTime = "";
		}
		
		if (queryType.contains("ask"))
			if (client.hasResult(query))
			{	
				return;
			}
			else
				query = "";
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
		int maxVar = 2;
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
		while (var<maxVar)
		{
		  try {	
			keys = assignedVars.keySet();
			keySelected = randomFilter.getRandomValue(keys);
			value = assignedVars.get(keySelected);
			
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
