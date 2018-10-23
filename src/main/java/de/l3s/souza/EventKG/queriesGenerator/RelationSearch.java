package de.l3s.souza.EventKG.queriesGenerator;

import java.io.IOException;
import java.util.Map;

import de.l3s.elasticquery.ElasticMain;
import de.l3s.elasticquery.RelationSnapshot;

public class RelationSearch {

	private ElasticMain es;
	private Map<String,RelationSnapshot> results; 
	private String field;
	private String keyword;
	private String indexName;
	private int limit;
	
	public RelationSearch(String keyword, String indexName, String field, int limit, ElasticMain es) throws IOException {
		
		this.keyword = keyword;
		this.indexName = indexName;
		this.field = field;
		this.limit = limit;
		this.es = es;
		
	}
	
	public RelationSearch (String indexName, ElasticMain es) throws IOException
	{
		this.indexName = indexName;
		this.es = es;
	}

	public Map<String, RelationSnapshot> getRelations () throws IOException
	{
		es.setField(field);
		es.setKeywords(keyword);
		es.setIndexName(indexName);
		es.setRandomSearch(false);
		es.setLimit(limit);
		es.run();
		results = es.getRelationMap();
		return results;
	}
	
	public String getField() {
		return field;
	}

	public void setField(String field) {
		es.setField(field);
		this.field = field;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
		es.setKeywords(keyword);


	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
		es.setIndexName(indexName);

	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
		es.setLimit(limit);
	}

	public boolean isEmpty ()
	{
		
		return es.getRelationMap().isEmpty();
	}
}
