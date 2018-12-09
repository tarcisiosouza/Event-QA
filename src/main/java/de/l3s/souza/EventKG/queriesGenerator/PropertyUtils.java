package de.l3s.souza.EventKG.queriesGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyUtils {
	
	private Map<String,String> wikidataProp;
	
	public PropertyUtils() {
		
	//	this.wikidataProp = wikidataProp;
	}
	
	public String getOwlSameAs (String id, String language) throws IOException
	{
		/*
		String attrib = getAttributes (id);*/
		String owlSameAs = "";
		try {
		String urId = id.replaceAll("\\<", "");
		urId = urId.replaceAll("\\>", "");
		   
	    String urlString = "http://eventkginterface.l3s.uni-hannover.de/resource/" + urId;
	    // create the url
	    URL url = new URL(urlString);
	    
	    // open the url stream, wrap it an a few "readers"
	    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

	    // write the output to stdout
	    String line;
	    while ((line = reader.readLine()) != null)
	    {
	      if (line.contains("<a href=\"http://dbpedia.org") || line.contains("<a href=\"http://www.wikidata.org") || line.contains("<a href=\"http://yago-knowledge.org"))
	      {
	    	  	final Pattern pattern = Pattern.compile("\"(.+?)\"");
				final Matcher m = pattern.matcher(line);
				m.find();
				return ("<"+m.group(1)+">");

	      }
	    }
		} catch (Exception e)
		{
			return "";
		}
	/*
		owlSameAs = getValueFromDocument (attrib, "owl:sameAs", language);
		
		if (owlSameAs.isEmpty()) 
		{
			owlSameAs = getValueFromDocument (attrib, "owl:sameAs", " ");

		}
*/
		return owlSameAs;
		
	}
	
	public String getLabelFirstEnglishProperty (String id) throws IOException //return the first label in other language if @en is not in there
	{
		String wikiId;
		String wikiLabel = "";
		
		String document = wikidataProp.get(id);
		
			wikiLabel = getValueFromDocument (document, "rdfs:label", "@en");
			if (wikiLabel.isEmpty())
			{
				wikiLabel = getValueFromDocument (document, "rdfs:label", " ");
			}
		
		return wikiLabel;
	}
	
	
	public String getAttributes (String id) throws IOException
	{
	/*	es.setKeywords(id);
		es.setIndexName("souza_eventkg");
		es.setRandomSearch(false);
		es.setField("entityid");
		es.setLimit(1);
		es.run();
 	
 		Map<String,String> document = es.getGenericDocuments();
		
 		for (Entry<String,String> entry : document.entrySet())
		{
 			return entry.getValue();
		}
		*/
 		return "no attrib";
	}
	
	public String getValueFromDocument (String doc, String prop, String language)
	{
		String label = "";
		String lang = language;
		final Pattern pattern;
		final Matcher matcher;
		
				StringTokenizer tokenValue = new StringTokenizer (doc,"\n");
			
				if (prop.contains("sameAs"))
				{
					if (!lang.contentEquals(" "))
						lang = "dbpedia_en";
				}
				while (tokenValue.hasMoreTokens())
				{
					String currentTokenValue = tokenValue.nextToken();
					if (currentTokenValue.contains(prop) && currentTokenValue.contains(lang))
					{
						StringTokenizer tokenCurrentLine = new StringTokenizer (currentTokenValue);
						if (prop.contains("label"))
						{
							pattern = Pattern.compile("\"(.+?)\"");
							matcher = pattern.matcher (currentTokenValue);
							matcher.find();
							label = tokenCurrentLine.nextToken() + " \"" + matcher.group(1) + "\"" + lang;
						}
						if (prop.contains("sameAs"))
						{
							label = tokenCurrentLine.nextToken();
							label = tokenCurrentLine.nextToken();
							
						}
						
						if (prop.contains("sem:hasBeginTimeStamp") || prop.contains("sem:hasEndTimeStamp"))
						{
							label = tokenCurrentLine.nextToken();
							label = label + " " + tokenCurrentLine.nextToken();
						}
						break;
					}
				}
					
		return label;
			
		
	}
	
	public String getLabelFirstEnglishEntity (ArrayList<String> entities) throws IOException
	{
		
		
		
		String entityId;
		String entityLabel = "";
		int i = 0;
		for (i =0;i<entities.size();i++)
		{
			entityId = entities.get(i);
			StringTokenizer tokenEntityId = new StringTokenizer (entityId);
		
			entityId = tokenEntityId.nextToken();
			entityId = tokenEntityId.nextToken();
	/*	
			es.setKeywords(entityId);
			es.setIndexName("souza_eventkg");
			es.setRandomSearch(false);
			es.setLimit(1);
			es.run();
	 */		
	 	//		Map<String,String> document = es.getGenericDocuments();
			
			Map<String,String> document = null;
	 			for (Entry<String,String> entry : document.entrySet())
	 			{
	 				String value = entry.getValue();
	 				StringTokenizer tokenValue = new StringTokenizer (value,"\n");
	 			
	 				while (tokenValue.hasMoreTokens())
	 				{
	 					String currentTokenValue = tokenValue.nextToken();
	 					if (currentTokenValue.contains("rdfs:label") && currentTokenValue.contains("@en"))
	 					{
	 						final Pattern pattern = Pattern.compile("\"(.+?)\"");
	 						final Matcher matcher = pattern.matcher (currentTokenValue);
	 						matcher.find();
	 						StringTokenizer tokenCurrentLine = new StringTokenizer (currentTokenValue);
	 						
	 						entityLabel = tokenCurrentLine.nextToken() + " \"" + matcher.group(1) + "\"" + "@en";
	 						i = entities.size() + 1;
	 						break;
	 					}
	 				}
	 			
	 			 }
		}
		return entityLabel;
	}
	
	
	
	public String getLabelFirstEnglishEntity (String id) throws IOException //return the first label in other language if @en is not in there
	{
		
		String urId = id.replaceAll("\\<", "");
		urId = urId.replaceAll("\\>", "");
		   
	    String urlString = "http://eventkginterface.l3s.uni-hannover.de/resource/" + urId;
	    // create the url
	  try {
	    URL url = new URL(urlString);
	    
	    // open the url stream, wrap it an a few "readers"
	    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

	    // write the output to stdout
	    String line;
	    while ((line = reader.readLine()) != null)
	    {
	      if (line.contains("<a href=\"http://dbpedia.org") || line.contains("<a href=\"http://www.wikidata.org") || line.contains("<a href=\"http://yago-knowledge.org"))
	      {
	    	  	final Pattern pattern = Pattern.compile("\"(.+?)\"");
				final Matcher m = pattern.matcher(line);
				m.find();
				return (m.group(1));

	      }
	    }
	  } catch (Exception e)
	  {
		  return "";
	  }
		return "";
	}
	
	public String getTextLabelFirstEnglishEntity (String id) throws IOException //return the first label in other language if @en is not in there
	{
	
			String urId = id.replaceAll("\\<", "");
			urId = urId.replaceAll("\\>", "");
			   
		    String urlString = "http://eventkginterface.l3s.uni-hannover.de/resource/" + urId;
		    // create the url
		    try {
		    URL url = new URL(urlString);
		    
		    // open the url stream, wrap it an a few "readers"
		    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

		    // write the output to stdout
		    String line;
		    while ((line = reader.readLine()) != null)
		    {
		      if (line.contains("<a href=\"http://dbpedia.org") || line.contains("<a href=\"http://www.wikidata.org") || line.contains("<a href=\"http://yago-knowledge.org"))
		      {
		    	  	final Pattern pattern = Pattern.compile("\"(.+?)\"");
					final Matcher m = pattern.matcher(line);
					m.find();
					return (m.group(1));

		      }
		    }
		    
		    reader.close();

		    } catch (Exception e)
		    {
		    	
		    	return "";
		    }
		    // close our reader
		    
		/*  
		String entityId;
		String entityLabel = "";
		int i = 0;
		/*
			es.setKeywords(id);
	 		es.setIndexName("souza_eventkg");
	 		es.setRandomSearch(false);
	 		es.setLimit(1);
	 		es.run();
	 	*/	
	 		//	Map<String,String> document = es.getGenericDocuments();
				
		/*	Map<String,String> document = null;
			

	 			entityLabel = getLabelFromDocument (document, "@en");
	 			if (entityLabel.isEmpty())
 				{
		 			entityLabel = getLabelFromDocument (document, " ");
 				}
		
	 	final Pattern pattern = Pattern.compile("\"(.+?)\"");
		final Matcher matcher = pattern.matcher (entityLabel);
		matcher.find();
		entityLabel = matcher.group(1);
		*/
		return "";
	}
	
	
	private static String getLabelFromDocument (Map<String,String> doc, String language)
	{
		String label = "";
		for (Entry<String,String> entry : doc.entrySet())
			{
			
				String value = entry.getValue();
				StringTokenizer tokenValue = new StringTokenizer (value,"\n");
			
				while (tokenValue.hasMoreTokens())
				{
					String currentTokenValue = tokenValue.nextToken();
					if (currentTokenValue.contains("rdfs:label") && currentTokenValue.contains(language))
					{
						final Pattern pattern = Pattern.compile("\"(.+?)\"");
						final Matcher matcher = pattern.matcher (currentTokenValue);
						matcher.find();
						StringTokenizer tokenCurrentLine = new StringTokenizer (currentTokenValue);
						
						label = tokenCurrentLine.nextToken() + " \"" + matcher.group(1) + "\"" + language;
						
						break;
					}
				}
			
				
			 }
		
		return label;
			
		
	}

}
