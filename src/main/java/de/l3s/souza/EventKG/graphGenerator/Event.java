package de.l3s.souza.EventKG.graphGenerator;

public class Event {
	private String type;
	private String dbpedia;
	
	public Event(String type, String dbpedia) {
		super();
		this.type = type;
		this.dbpedia = dbpedia;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDbpedia() {
		return dbpedia;
	}
	public void setDbpedia(String dbpedia) {
		this.dbpedia = dbpedia;
	}
	
}
