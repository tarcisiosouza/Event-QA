package de.l3s.souza.EventKG.queriesGenerator.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import de.l3s.souza.EventKG.queriesGenerator.PropertyUtils;
import de.l3s.souza.EventKG.queriesGenerator.relation.RelationSnapshot;

public class NlpConversion {
	
	private String timeFilterExpression;
	private NaturalLanguageQuery query;
	private Map<String,String> typesEntities;
	private PropertyUtils propertyUtils;
	
	public NlpConversion(Map<String,String> typesEntities,PropertyUtils propertyUtils) {
		
		timeFilterExpression = "";
		this.typesEntities = typesEntities; 
		this.propertyUtils = propertyUtils;
		query = new NaturalLanguageQuery ();
	}

	public void setQuestionType (String questionType)
	{
		query.setQueryType(questionType);
	}
	
	public void setGivenVars (Map<String,String> map)
	{
		query.setGivenVariables(map);
	}
	public String getTimeFilterExpression() {
		return timeFilterExpression;
	}

	public void setTimeFilterExpression(String timeFilterExpression) {
		this.timeFilterExpression = timeFilterExpression;
		query.setTimeFilterExpression(timeFilterExpression);
	}

	public void buildTimeFilterExpression (TimeFilterSnapshot timeFilterSnapshot)
	{
		switch (timeFilterSnapshot.getPeriod())
		{
			case "sameStartTime":
			{
				if (!timeFilterSnapshot.getEventDecision().contains("specific"))
					timeFilterExpression = timeFilterExpression + " at the day " + timeFilterSnapshot.getBeginTimeStamp();
				break;
			}
			case "sameRange":
			{
				if (!timeFilterSnapshot.getEventDecision().contains("specific"))
				{	
					if (!timeFilterSnapshot.getEndTimeStamp().isEmpty())
						timeFilterExpression = timeFilterExpression + " from  " + timeFilterSnapshot.getBeginTimeStamp() + " and  " + timeFilterSnapshot.getEndTimeStamp();
					else
						timeFilterExpression = timeFilterExpression + " in  " + timeFilterSnapshot.getBeginTimeStamp();

				}
			
				break;
			}
			
			case "before":
			{
				if (!timeFilterSnapshot.getEventDecision().contains("specific"))
					timeFilterExpression = timeFilterExpression + " before  " + timeFilterSnapshot.getBeginTimeStamp() ;

				break;
			}
			
			case "after":
			{
				
				if (!timeFilterSnapshot.getEndTimeStamp().isEmpty())
				{
					
					if (!timeFilterSnapshot.getEventDecision().contains("specific"))
						timeFilterExpression = timeFilterExpression + " after  " + timeFilterSnapshot.getEndTimeStamp();
				}	
				else
				{
				
					if (!timeFilterSnapshot.getEventDecision().contains("specific"))
						timeFilterExpression = timeFilterExpression  + " has started after  " + timeFilterSnapshot.getEndTimeStamp();
				}
				break;
			}
			
			case "last":
			{	
				if (!timeFilterSnapshot.getEventDecision().contains("specific"))
					timeFilterExpression = timeFilterExpression + " last " + timeFilterSnapshot.getLast() +" years ago" ;
				
				break;
			}
				
			case "year":
			{
				int beginYear = Integer.parseInt(timeFilterSnapshot.getYearSelected());
				
				if (!timeFilterSnapshot.getEventDecision().contains("specific"))
					timeFilterExpression = timeFilterExpression + " in " + beginYear ;
				
				break;
			}
		}
		
		query.setTimeFilterExpression(timeFilterExpression);
	}
	
	private String getNlRoleType (RelationSnapshot r1) throws IOException
	{
	
		String roleType;
		
		if (r1.getRoleType().contains("wdt"))
		{
			roleType = propertyUtils.getLabelFirstEnglishProperty(r1.getRoleType());
		}
		else
		{
			StringTokenizer token = new StringTokenizer (r1.getRoleType());
			roleType = token.nextToken();
			roleType = token.nextToken();
		}
		
		return roleType;
	}
	
	public void setEntityNaturalLanguage (String var, RelationSnapshot r, String elementRelation) throws IOException
	{
		
		Entity entity;
		Relation relation = new Relation ();
		String relationId = r.getId();
		String type = "";
		String entityId = getIdElementRelation (r,elementRelation);
		String labelEntity =  propertyUtils.getTextLabelFirstEnglishEntity(entityId);
		String roleType = getNlRoleType (r);
		
		if (typesEntities.containsKey(entityId))
			type = typesEntities.get(entityId);
		
		entity = new Entity(type, labelEntity,var, false);
		
		if (query.getRelations().containsKey(relationId))
		{
			relation=query.getRelations().get(relationId);
			if (elementRelation.contains("object"))
			{
				relation.setObject(entity);
			}
			else
			{
				relation.setSubject(entity);
			}
			query.setRelationById(r.getId(), relation);
		}
		else
		{
			if (elementRelation.contains("object"))
				relation.setObject(entity);
			else
				relation.setSubject(entity);
			
			relation.setId(r.getId());
			relation.setRoleType(roleType);
			query.setRelationById(r.getId(), relation);

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
	
	public void setVariables (ArrayList<String> variables)
	{
		Map <String,Relation> relations = query.getRelations();
	
			for (Entry<String,Relation> entry : relations.entrySet())
			{
				Relation currentRelation = entry.getValue();
				Entity object = currentRelation.getObject();
				Entity subject = currentRelation.getSubject();
				
				if (variables.contains(object.getVarName()))
				{
					object.setVar(true);
					currentRelation.setObject(object);
				}
				
				if (variables.contains(subject.getVarName()))
				{
					subject.setVar(true);
					currentRelation.setSubject(subject);
				}
				
				query.setRelationById(currentRelation.getId(), currentRelation);
			}
			
			query.setVariables(variables);
	}
	
	public void setCountVar (String varName)
	{
		Map <String,Relation> relations = query.getRelations();
	
			for (Entry<String,Relation> entry : relations.entrySet())
			{
				Relation currentRelation = entry.getValue();
				Entity object = currentRelation.getObject();
				Entity subject = currentRelation.getSubject();
				
				if (varName.contentEquals(object.getVarName()))
				{
					object.setCountVar(true);
					currentRelation.setObject(object);
				}
				
				if (varName.contentEquals(subject.getVarName()))
				{
					subject.setCountVar(true);
					currentRelation.setSubject(subject);
				}
				
				query.setRelationById(currentRelation.getId(), currentRelation);
			}
	}
	
	public String getNaturalLanguage ()
	{
		return query.toString();
	}
}

