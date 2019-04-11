package de.l3s.souza.EventKG.queriesGenerator.relation;

import java.io.IOException;
import java.util.StringTokenizer;

import de.l3s.souza.EventKG.queriesGenerator.PropertyUtils;

public class RelationUtils {
	
	private PropertyUtils propertyUtils;
	
	public RelationUtils(PropertyUtils propertyUtils) {
		
		this.propertyUtils = propertyUtils;
	}
	
	public RelationUtils() {
		
		
	}

	public String getRoleTypeRelation (String triple)
	{
		String output = triple;
		
		StringTokenizer token = new StringTokenizer (output);
		output = token.nextToken() + " " + token.nextToken();
		return output;
		
	}
	
	public String getLiteralRoleTypeRelation (String triple)
	{
		String output = triple;
		
		StringTokenizer token = new StringTokenizer (output);
		token.nextToken();
		output = token.nextToken();
		return output.replaceAll(output.substring(0, 5), "");
		
	}
	
	public RelationSnapshot initializeRelation (RelationSnapshot relation)
	{
		RelationSnapshot relationSnapshot = relation;
		
		String attributes = relationSnapshot.getAttributes();
	
		StringTokenizer tokenAttrib = new StringTokenizer (attributes,"\n");
		
		while (tokenAttrib.hasMoreTokens())
		{
			String currentLine = tokenAttrib.nextToken();
			
			if (currentLine.contains("type"))
			{
				relationSnapshot.setType(currentLine);
			}
			if (currentLine.contains("subject"))
			{
				relationSnapshot.setSubject(currentLine);
			}
			if (currentLine.contains("object"))
			{
				relationSnapshot.setObject(currentLine);
			}
			if (currentLine.contains("roleType"))
			{
				relationSnapshot.setRoleType(currentLine);
			}
			if (currentLine.contains("sem:hasBeginTimeStamp"))
			{
				relationSnapshot.setBeginTimestamp(currentLine);
			}
			if (currentLine.contains("sem:hasEndTimeStamp"))
			{
				relationSnapshot.setEndTimestamp(currentLine);
			}
			if (currentLine.contains("hasPlace"))
			{
				relationSnapshot.addHasPlace(currentLine);
			}
			if (currentLine.contains("previousEvent"))
			{
				relationSnapshot.setPreviousEvent(currentLine);
			}
			if (currentLine.contains("nextEvent"))
			{
				relationSnapshot.setNextEvent(currentLine);
			}
			if (currentLine.contains("label"))
			{
				relationSnapshot.addElementLabel(currentLine);
			}
			if (currentLine.contains("subEvent"))
			{
				relationSnapshot.addElementSubEvent(currentLine);
			}
			
			if (currentLine.contains("owl"))
			{
				relationSnapshot.addElementOwl(currentLine);
			}
			
			if (currentLine.contains("description"))
			{
				relationSnapshot.addDescription(currentLine);
			}
		}
		
		return relationSnapshot;
		
	}
	public String getRoleType (RelationSnapshot relationSnapshot) throws IOException
	{
		String roleType = "";
		roleType = relationSnapshot.getRoleType();
		StringTokenizer tokenRole = new StringTokenizer (roleType);
		roleType = tokenRole.nextToken();
		roleType = tokenRole.nextToken();
		
		return (roleType.contains("wdt") ? propertyUtils.getLabelFirstEnglishProperty(roleType): roleType);
	}
	
	public String getRoleTypeWithProperty (RelationSnapshot relationSnapshot) throws IOException
	{
		String roleType = "";
		roleType = relationSnapshot.getRoleType();
		StringTokenizer tokenRole = new StringTokenizer (roleType);
		roleType = tokenRole.nextToken();
		roleType = roleType + " " + tokenRole.nextToken();
		
		return (roleType);
	}
	
	
	public RelationSnapshot updatePredicateRelation (String predicateType,String predicateRelation, RelationSnapshot relation)
	{
		RelationSnapshot updatedRelation = new RelationSnapshot ();
		updatedRelation = relation;
		
		switch (predicateType)
		{
			case "type":
			{
				updatedRelation.setType(predicateRelation);
				break;
			}
			
			case "subject":
			{
				updatedRelation.setSubject(predicateRelation);
				break;
			}
			
			case "label":
			{
				updatedRelation.addElementLabel(predicateRelation);
				break;
			}
			
			case "owl":
			{
				updatedRelation.addElementOwl(predicateRelation);
				break;
			}
			case "object":
			{
				updatedRelation.setObject(predicateRelation);
				break;
			}
			
			case "begintimestamp":
			{
				updatedRelation.setBeginTimestamp(predicateRelation);
				break;
			}
			
			case "endtimestamp":
			{
				updatedRelation.setEndTimestamp(predicateRelation);
				break;
			}
			
			case "roletype":
			{
				updatedRelation.setRoleType(predicateRelation);
				break;
			}
		}
		
		return updatedRelation;
		
	}
	
	public String getSubObjIdFromRelation(RelationSnapshot relationSnapshot, String type)
	{
		String output = "";
		
		if (type.contains("subject"))
			output = relationSnapshot.getSubject();
			
		if (type.contains("object"))
			output = relationSnapshot.getObject();
		
		StringTokenizer tokenSubjectId = new StringTokenizer (output);
		output = tokenSubjectId.nextToken();
		output = tokenSubjectId.nextToken();
		
		return output;
		
	}
	
	private RelationSnapshot getEventAttributeValues (RelationSnapshot relation)
	{
		RelationSnapshot r = relation;
		String attrib = r.getAttributes();
		
		StringTokenizer tokenAttrib = new StringTokenizer (attrib,"\n");
		
		while (tokenAttrib.hasMoreElements())
		{
			String nextAttrib = tokenAttrib.nextToken();
			if (nextAttrib.contains("nextEvent") || nextAttrib.contains("previousEvent"))
			{
				
			}
		}
		return r;
	}
	public RelationSnapshot addNodeRelation (String nodeType, RelationSnapshot relation,String nodeId, String varName) throws IOException
	{
		RelationSnapshot r = relation;
		
		String nodeLabel = propertyUtils.getOwlSameAs (nodeId,"@en");
		
		String attributes = "";
		Node node = new Node (nodeType,nodeLabel,varName);
		node.setAttributes(attributes);
	/*	
		if (nodeType.contains("event"))
			r = getEventAttributeValues (r);
		*/
		r.addNode(node);
		
		return r;
		
	}
}
