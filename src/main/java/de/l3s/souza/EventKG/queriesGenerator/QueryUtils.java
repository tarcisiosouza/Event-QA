package de.l3s.souza.EventKG.queriesGenerator;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import de.l3s.souza.EventKG.queriesGenerator.relation.RelationSnapshot;
import de.l3s.souza.EventKG.queriesGenerator.relation.RelationUtils;

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
	
	private QueryBuilder queryBuilder;
	private HashSet<String> pairRelationsRoletypeEntity;
	
	public QueryUtils(RelationSnapshot relation, String eventAttributes, String obId, String subId, 
			TimeStampUtils ts, RelationUtils relationUtils, PropertyUtils propertyUtils, 
			 Map<String,String> events,HashSet<String> pairRelationsRoletypeEntity,String queryType,Map<String,String> typesEntities,int generated) throws IOException {
		
		this.relation = relation;
		this.pairRelationsRoletypeEntity = pairRelationsRoletypeEntity;
		randomFilter = new RandomFilter ();
		this.eventAttributes = eventAttributes;
		this.obId = obId;
		this.subId = subId;
	
		queryBuilder = new QueryBuilder (relationUtils, relation, propertyUtils, ts, eventAttributes,queryType,typesEntities,generated);
		
	}

	public QueryUtils(RelationSnapshot relation1,RelationSnapshot relation2, String eventAttributes, String objectIdr1, String objectIdr2,
			String subjectIdr1, String subjectIdr2,
			TimeStampUtils ts, RelationUtils relationUtils, PropertyUtils propertyUtils, Map<String,String> events, String queryType,Map<String,String> typesEntities,int generated) throws IOException {
		
		this.relation1 = relation1;
		this.relation2 = relation2;
		objectIdRelation1 = objectIdr1;
		objectIdRelation2 = objectIdr2;
		subjectIdRelation1 = subjectIdr1;
		subjectIdRelation2 = subjectIdr2;
		randomFilter = new RandomFilter ();
		this.eventAttributes = eventAttributes;
//		relationSearch = new RelationSearch ("souza_eventkg_relations_all");
		queryBuilder = new QueryBuilder (relationUtils, relation1,relation2, propertyUtils, ts, eventAttributes,queryType,typesEntities,generated);
		
	}

	public String getQueryType ()
	{
		return queryBuilder.getQueryType();
	}
	
	public String getNaturalLanguage ()
	{
		return queryBuilder.getNaturalLanguage();
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

	
	public void setPairRelationsRoletypeEntity(HashSet<String> pairRelationsRoletypeEntity) {
		this.pairRelationsRoletypeEntity = pairRelationsRoletypeEntity;
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
		return queryBuilder.getQueryFromRelations(relation1,relation2);
	}
	
	public String getQuery ()
	{
		return query;
	}

	public String getQueryName() {
			
		return queryBuilder.getQueryName();
	}

}
