package de.l3s.souza.EventKG.queriesGenerator.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class NaturalLanguageQuery {
	
	private String questionType;
	private String timeFilterExpression;
	private Map<String,String> givenVariables;
	private Map<String,Relation> relations;
	private ArrayList<String> variables;
	public String getQueryType() {
		return questionType;
	}

	public ArrayList<String> getVariables() {
		return variables;
	}

	public void setVariables(ArrayList<String> variables) {
		this.variables = variables;
	}

	public Map<String, String> getGivenVariables() {
		return givenVariables;
	}

	public void setGivenVariables(Map<String, String> givenVariables) {
		this.givenVariables = givenVariables;
	}

	public String getTimeFilterExpression() {
		return timeFilterExpression;
	}

	public void setTimeFilterExpression(String timeFilterExpression) {
		this.timeFilterExpression = timeFilterExpression;
	}

	public NaturalLanguageQuery() {
		
		questionType ="";
		variables = new ArrayList<String>();
		timeFilterExpression = "";
		relations = new HashMap<String,Relation> ();
		givenVariables = new HashMap<String,String>();
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public Map<String, Relation> getRelations() {
		return relations;
	}

	public void setQueryType(String clause) {

		switch (clause) {
		case "select": {
			questionType = "What ";
			break;
		}
		case "ask": {
			questionType = "Did ";
			break;
		}

		case "count": {
			questionType = "How many ";
			break;
		}

		}
	}

	public void setRelationById (String id, Relation r)
	{
		relations.put(id, r);
	}
	
	public void setVariablesRelations ()
	{
		
	}
	
	@Override
	public String toString() {
		
		String naturalLanguageVars = "";
		String naturalLanguageGivenVars = "";
		HashMap<String,Entity> vars = new HashMap<String,Entity>();
		String naturalLanguagePredicates = "";
		int relationNumber = 1;
		String role ;
		String type ;
		for (Entry<String, Relation> entry: relations.entrySet())
		{
			Relation r = entry.getValue();
			Entity object = r.getObject();
			Entity subject = r.getSubject();
			
			if (object.isVar() && !vars.containsKey(object.getVarName()))
			{
				 role = r.getRoleType().replaceAll("dbo:", "");
				 type =  subject.getType().replaceAll("dbo:", "");
				vars.put(object.getVarName(),object);
				
				if (subject.isVar())
				naturalLanguagePredicates = naturalLanguagePredicates + object.getVarName() + 
						" was the " + role + " of " + type + " " + subject.getVarName() + "\n";
				else
				{
					String labelSubj =  subject.getLabel();
					naturalLanguagePredicates = naturalLanguagePredicates + object.getVarName() + 
							" was the " + role + " of " + labelSubj + "\n";
					
				}
				relationNumber++;
				continue;
			}
			if (subject.isVar() && !vars.containsKey(subject.getVarName()))
			{
				role = r.getRoleType().replaceAll("dbo:", "");
				type =  subject.getType().replaceAll("dbo:", "");
				vars.put(subject.getVarName(),subject);
				
				if (object.isVar())
					naturalLanguagePredicates = naturalLanguagePredicates + object.getVarName() + 
						" was the " + role + " of " + type + " " + subject.getVarName() + "\n";
				else
				{
					String labelObj =  object.getLabel();
					naturalLanguagePredicates = naturalLanguagePredicates + labelObj + 
					" was the " + role + " of " + type + " " + subject.getVarName() + "\n";
				}
					relationNumber++;
				continue;
			}
		}
		
		
		/*if (questionType.contains("What") || questionType.contains("How many"))
		{*/
			
			for (Entry<String,Entity> entry : vars.entrySet())
			{
				type =  entry.getValue().getType().replaceAll("dbo:", "");
				
				if (entry.getValue().isEvent())
				{
					if (entry.getValue().isCountVar())
						naturalLanguageVars = naturalLanguageVars + entry.getValue().getVarName() +" is a "+type + "(How many " + type + ")\n";
					else
						naturalLanguageVars = naturalLanguageVars + entry.getValue().getVarName() +" is a "+type 
						+ "\n" ;
				}
				else
				{
					if (entry.getValue().isCountVar())
						naturalLanguageVars = naturalLanguageVars + entry.getValue().getVarName() +" is a "+type 
						+ "(How many " + type + ")\n";
					else
						naturalLanguageVars = naturalLanguageVars + entry.getValue().getVarName() +" is a "+type + "\n";
				}
			}
			
			if (vars.size()<=1)
			{
				for (int i=0;i<variables.size();i++)
				{
					if (variables.contains("StartTime"))
					naturalLanguageVars = naturalLanguageVars + " " + variables.get(i) + " is the start date filter of " + variables.get(i).replaceAll("StartTime", "") + "\n";
					if (variables.contains("EndTime"))
						naturalLanguageVars = naturalLanguageVars + " " + variables.get(i) + " is the end date filter of " + variables.get(i).replaceAll("EndTime", "") + "\n";
				}
			}
				
		//}
		
			
		for (Entry<String,String> variable:givenVariables.entrySet())
		{
			naturalLanguageGivenVars = naturalLanguageGivenVars + variable.getKey() + " is given: " + variable.getValue() + "\n";
		}
		
		String previousString = naturalLanguageVars + naturalLanguagePredicates + naturalLanguageGivenVars + timeFilterExpression + "\n";
		
		StringTokenizer token = new StringTokenizer (previousString,"\n");
		
		
		
		String finalString = "";
		while (token.hasMoreTokens())
		{
			String current = token.nextToken();
			if (!current.contains("rdf:type"))
				finalString = finalString + current + "\n";
		}
		
	/*	if (timeFilterExpression.length() < 14 && timeFilterExpression.contains("filter in"))
			finalString = "";*/
		return finalString + "\n";
	}
}
