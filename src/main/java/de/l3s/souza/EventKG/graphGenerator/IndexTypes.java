package de.l3s.souza.EventKG.graphGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class IndexTypes {
	private static Map<String, String> data = new HashMap<String, String>();

	public Map<String,String> getMap () throws IOException
	{
		//readFile ("/home/souza/EventKG/data/output/types.nq");
    	readFile ("/home/souza/EventKG/data/output/types_dbpedia.nq");	    	
		return data;
	}
	
	public void deleteData ()
	{
		data.clear();
	}
	public static void readFile (String path) throws IOException
	{
		File fileEntities = new File (path);
		FileReader frEntities = new FileReader (fileEntities);
		BufferedReader br = new BufferedReader (frEntities);
		
		String lineEntities = "";
		System.out.println("reading file...: " + fileEntities.getName());
		while ((lineEntities=br.readLine())!=null)
    	{
    		
    		if (lineEntities.contains("@prefix") || lineEntities.contains("@base") || lineEntities.isEmpty())
    			continue;
    		
    		StringTokenizer tokenEntities = new StringTokenizer (lineEntities, "\n");
    		while (tokenEntities.hasMoreElements())
    		{
    			StringTokenizer tokenId = new StringTokenizer (lineEntities);
    			String id = tokenId.nextToken();
    			
    				if (data.containsKey(id))
    				{
    					String currentAtt = data.get(id);
    					currentAtt = currentAtt + "\n" + lineEntities.replace(id + " ", "");
    					data.put(id, currentAtt);
    				}
    				else
    				{
    					String type = tokenId.nextToken();
    					type = tokenId.nextToken();
    					data.put(id, type);
    				}
    				break;
    		}
    		
    	}
		
		
	}
		
}
