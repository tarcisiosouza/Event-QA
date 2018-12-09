package de.l3s.souza.EventKG.queriesGenerator;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.commons.io.IOUtils;

public class EventkgClient {

	private boolean hasException;
	private String lines;
	private int valueCount;
	
	public int getValue() {
		return valueCount;
	}

	public EventkgClient() {
		hasException = false;
		lines = "";
	}


	public boolean hasResult(String incomingQuery) throws InterruptedException {

	    ExecutorService executor = Executors.newFixedThreadPool(4);

		String query = "";
		String prefix = "";
		try {

			prefix = "PREFIX eventKG-r: <http://eventKG.l3s.uni-hannover.de/resource/>\n"
					+ "PREFIX eventKG-s: <http://eventKG.l3s.uni-hannover.de/schema/>\n"
					+ "PREFIX eventKG-g: <http://eventKG.l3s.uni-hannover.de/graph/>\n"
					+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "PREFIX so: <http://schema.org/>\n"
					+ "PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/>\n"
					+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
					+ "PREFIX wdt: <http://www.wikidata.org/prop/direct/>"
					+ "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
					+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" + "PREFIX dbr: <http://dbpedia.org/resource/>\n"
					+ "PREFIX dbpedia-de: <http://de.dbpedia.org/resource/>\n"
					+ "PREFIX dcterms: <http://purl.org/dc/terms/>\n" ;

			query = prefix + incomingQuery;
			
			final String uri = "http://eventkginterface.l3s.uni-hannover.de/sparql?default-graph-uri=&query="
					+ URLEncoder.encode(query, "UTF-8") + "&format=json";

			 Future<?> future = executor.submit(new Runnable() {
			        @Override
			        public void run() {
			        	try {
							readLines(uri);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}    
			        }
			    });

			    executor.shutdown();            //        <-- reject all further submissions

			    try {
			        future.get(5, TimeUnit.SECONDS);  //     <-- wait 2 seconds to finish
			    } catch (InterruptedException e) {    //     <-- possible error cases
			    //    System.out.println("job was interrupted");
			    } catch (ExecutionException e) {
			  //      System.out.println("caught exception: " + e.getCause());
			    } catch (TimeoutException e) {
			        future.cancel(true);              //     <-- interrupt the job
			        System.out.println("timeout");
			        Thread.sleep(5000);
			    }

			    // wait all unfinished tasks for 2 sec
			    if(!executor.awaitTermination(5, TimeUnit.SECONDS)){
			        // force them to quit by interrupting
			        executor.shutdownNow();
			    }
			    

			//readLines(uri);
			if (lines.contains("excep") || lines.isEmpty())
			{
				hasException = true;
				return false;
			}
			
			if (lines.contains("\"boolean\": true") || lines.contains("\"boolean\": false"))
				return true;
			
			JSONObject json = new JSONObject(lines);
			
			JSONArray arr = json.getJSONObject("results").getJSONArray("bindings");
		
			if (arr.toString().contains("{\"count\":"))
			try {
				
				for (int i=0; i<arr.length(); i++)
				{
				    JSONObject node = arr.getJSONObject(i);     
				    valueCount = node.getJSONObject("count").getInt("value");
				}
			} catch (Exception e)
			{
		
			}			 
			/*for (Object o : arr) {
		        JSONObject jsonLineItem = (JSONObject) o;
		        String key = jsonLineItem.getString("key");
		        String value = jsonLineItem.getString("value");
		        
		    }*/
			
			if (arr.toString().contains("http://eventKG.l3s.uni-hannover.de/") || arr.toString().contains("http://www.w3.org")) {

				return true;
			}
			else
				return false;

		} catch (IOException e) {
			return false;
		} catch (JSONException e) {
			// this.entitiesByLabel.put(label, null);
			return false;
		}

		// this.entitiesByLabel.put(label, null);
		
	}
	
	public boolean hasException() {
		return hasException;
	}
	
	private static String GetJSONValue(String JSONString, String Field)
	{
	       return JSONString.substring(JSONString.indexOf(Field), JSONString.indexOf("\n", JSONString.indexOf(Field))).replace(Field+"\": \"", "").replace("\"", "").replace(",","");   
	}
	private void readLines(String uri) throws InterruptedException {

		lines = "";
		boolean succesfull = false;
		while (!succesfull) {
			try {
				lines = IOUtils.toString(new URL(uri), "UTF-8");
				succesfull = true;
			} catch (IOException e) {
			//	System.out.println("IOException in loadRelations. Repeat. URI: " + uri + ".");
				lines = "excep";
		        Thread.sleep(5000);
				
			}
		}

		
	}
}
