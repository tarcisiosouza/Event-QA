package de.l3s.souza.EventKG.graphGenerator;

import java.util.HashMap;

public class Entity {
	private String type;
	private HashMap<String,String> dbpediaEntries;
	
	public Entity(String type) {
		super();
		this.type = type;
		dbpediaEntries = new HashMap<String,String>();
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public void addDbpediaEntry (String key, String value)
	{
		
		dbpediaEntries.put(key, value);
	}
	
}
