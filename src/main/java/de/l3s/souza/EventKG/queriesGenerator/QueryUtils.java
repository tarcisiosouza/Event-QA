package de.l3s.souza.EventKG.queriesGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import de.l3s.elasticquery.ElasticMain;
import de.l3s.elasticquery.RelationSnapshot;

public class QueryUtils {
	
	private String obId;
	private String subId;
	private String objectIdRelation1;
	private String objectIdRelation2;
	private String subjectIdRelation1;
	private String subjectIdRelation2;
	private RelationSnapshot relation;
	private RelationSnapshot relation1;
	private RelationSnapshot relation2;
	private String eventAttributes;
	private RandomFilter randomFilter;
	private String query;
	private RelationSearch relationSearch;
	private QueryBuilder queryBuilder;
	private HashSet<String> pairRelationsRoletypeEntity;
	
	public QueryUtils(RelationSnapshot relation, String eventAttributes, String obId, String subId, 
			TimeStampUtils ts, RelationUtils relationUtils, PropertyUtils propertyUtils, 
			ElasticMain es, Map<String,String> events,HashSet<String> pairRelationsRoletypeEntity) throws IOException {
		
		this.relation = relation;
		this.pairRelationsRoletypeEntity = pairRelationsRoletypeEntity;
		randomFilter = new RandomFilter ();
		this.eventAttributes = eventAttributes;
		this.obId = obId;
		this.subId = subId;
		relationSearch = new RelationSearch ("souza_eventkg_relations_all", es);
		queryBuilder = new QueryBuilder (relationUtils, relation, propertyUtils, ts, eventAttributes);
		
	}

	public QueryUtils(RelationSnapshot relation1,RelationSnapshot relation2, String eventAttributes, String objectIdr1, String objectIdr2,
			String subjectIdr1, String subjectIdr2,
			TimeStampUtils ts, RelationUtils relationUtils, PropertyUtils propertyUtils, 
			ElasticMain es, Map<String,String> events) throws IOException {
		
		this.relation1 = relation1;
		this.relation2 = relation2;
		objectIdRelation1 = objectIdr1;
		objectIdRelation2 = objectIdr2;
		subjectIdRelation1 = subjectIdr1;
		subjectIdRelation2 = subjectIdr2;
		randomFilter = new RandomFilter ();
		this.eventAttributes = eventAttributes;
		relationSearch = new RelationSearch ("souza_eventkg_relations_all", es);
		queryBuilder = new QueryBuilder (relationUtils, relation1,relation2, propertyUtils, ts, eventAttributes);
		
	}

	
	public String getObId() {
		return obId;
	}

	public void setObId(String obId) {
		this.obId = obId;
	}


	public String getSubId() {
		return subId;
	}


	public void setSubId(String subId) {
		this.subId = subId;
	}

	public HashSet<String> getPairRelationsRoletypeEntity() {
		return pairRelationsRoletypeEntity;
	}

	public String getQueryFirstRelation () throws IOException, InterruptedException
	{
		return queryBuilder.getQueryFromRelations(relation,obId,subId);
	}
	
	public String getQuerySubGraph (String node) throws IOException, InterruptedException
	{
		RelationSnapshot r2;
		
		if (node.contains("entity"))
		{
			r2 = subGraphWalk (node);
			
			if (r2!=null)
			{
				if (pairRelationsRoletypeEntity.contains(relation.getRoleType() + " " +r2.getRoleType()))
					return "";
				else
					pairRelationsRoletypeEntity.add(relation.getRoleType() + " " +r2.getRoleType());
			}
			else return "";
				
			return queryBuilder.getQueryFromRelations(relation,r2);
		}
		else
			return queryBuilder.getQueryFromRelations(relation1,relation2);
	}
	
	public String getQuery ()
	{
		return query;
	}

	public RelationSnapshot subGraphWalk (String node) throws IOException
	{
		RelationSnapshot chosenRelation = null;
		Map<String, RelationSnapshot> subgraphRelations = new HashMap<String,RelationSnapshot> ();
		Map<String, RelationSnapshot> subgraphRelationsDbo = new HashMap<String,RelationSnapshot> ();

		if (node.contains("entity"))
		{
			if (obId.contains(node))
			{
			 
				relationSearch.setField("object");
				relationSearch.setKeyword(obId);
				relationSearch.setLimit(1000);
				subgraphRelations = relationSearch.getRelations();
			
				if (subgraphRelations.isEmpty())
				{
					relationSearch.setField("subject");
					subgraphRelations = relationSearch.getRelations();
				}
			
			}	
			
			if (subId.contains(node))
			{
			  if (node.contentEquals("entity"))
			  {
				relationSearch.setField("object");
				relationSearch.setKeyword(subId);
				subgraphRelations = relationSearch.getRelations();
				if (subgraphRelations.isEmpty())
				{
					relationSearch.setField("subject");
					subgraphRelations = relationSearch.getRelations();	
				}
				
			  }
				
			}
			
		}
			if (!(subgraphRelations.isEmpty()))
			{
				
				for (Entry<String,RelationSnapshot> entry : subgraphRelations.entrySet())
				{
					RelationSnapshot snapshot = entry.getValue();
					if (snapshot.getRoleType().contains("dbo"))
					{
						subgraphRelationsDbo.put(entry.getKey(), snapshot);
					}
						
				}
				
				if (!subgraphRelationsDbo.isEmpty())
					chosenRelation = randomFilter.getRandomValue(subgraphRelationsDbo);
			}
			
			return chosenRelation;
				
	}

	public String getQueryName() {
			
		return queryBuilder.getQueryName();
	}

}
