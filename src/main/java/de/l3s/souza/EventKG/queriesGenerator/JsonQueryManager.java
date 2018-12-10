package de.l3s.souza.EventKG.queriesGenerator;

public class JsonQueryManager {

	
	public String getJsonQuery (String query, int id)
	{
		String incomingQuery=query;
		if (query.contains("\""))
			incomingQuery=query.replace("\"", "\'");
		
		return ("{\n" + "\"id\":" + id + ",\n\"sparql_query\": \""+incomingQuery+" \"\n},");
	}
	

	public String getHeadJson()
	{
		return ("{\n" +
			"\"dataset\": {\n" + "\"prefix\": \"PREFIX eventKG-r: <http://eventKG.l3s.uni-hannover.de/resource/> PREFIX eventKG-s: "
					+ "<http://eventKG.l3s.uni-hannover.de/schema/> PREFIX eventKG-g: <http://eventKG.l3s.uni-hannover.de/graph/> "
					+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX so: <http://schema.org/> PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> "
					+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX wdt: <http://www.wikidata.org/prop/direct/> PREFIX dbo: <http://dbpedia.org/ontology/> "
					+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX dbr: <http://dbpedia.org/resource/> PREFIX dbpedia-de: <http://de.dbpedia.org/resource/> "
					+ "PREFIX dcterms: <http://purl.org/dc/terms/>\",\n" + 
					"\n" + 
					"\"queries\": [\n");
	}
	
	public String getEndJson ()
	{
		
		return ("		]\n" + 
				"	}\n" + 
				"}");
		
	}
}
