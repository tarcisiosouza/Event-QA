package de.l3s.souza.EventKG.queriesGenerator;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import de.l3s.elasticquery.ElasticMain;

public class ElasticSearchUtils {

	
	public ElasticSearchUtils() {
		
	}

	//get Attributes from Events or Entities (index name: souza_eventkg)
	public String getAttributes (String id, ElasticMain es) throws IOException
	{
		es.setKeywords(id);
 		es.setIndexName("souza_eventkg");
 		es.setRandomSearch(false);
 		es.setLimit(1);
 		es.run();
 		
 		Map<String,String> document = es.getGenericDocuments();
		
 		for (Entry<String,String> entry : document.entrySet())
		{
 			return entry.getValue();
		}
		
 		return "no attrib";
	}
	
}
