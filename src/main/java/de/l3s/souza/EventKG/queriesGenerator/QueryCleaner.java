package de.l3s.souza.EventKG.queriesGenerator;

import java.util.HashSet;
import java.util.StringTokenizer;

public class QueryCleaner {
	
	private HashSet<String> triples = new HashSet<String>();
	
	public String getCleanQuery (String query)
	{
		triples.clear();
		String newQuery = "";
		StringTokenizer token = new StringTokenizer (query,"\n");
		
		while (token.hasMoreTokens())
		{
			String nextToken = token.nextToken();
			if (nextToken.contains("rdf:type") || triples.contains(nextToken))
				continue;
			else
			{
				if (newQuery.isEmpty())
					newQuery =  newQuery + nextToken + "\n";
				else
					newQuery = newQuery + " " + nextToken  + "\n";
			}
			
			triples.add(nextToken);
		}
		
		return newQuery;
				
	}

}
