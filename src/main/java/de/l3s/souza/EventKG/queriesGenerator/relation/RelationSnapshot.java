package de.l3s.souza.EventKG.queriesGenerator.relation;

import java.util.ArrayList;

public class RelationSnapshot {

	private String id;
	private String type;
	private String subject;
	private String object;
	private String roleType;
	private String beginTimestamp;
	private String endTimestamp;
	private String previousEvent;
	private String nextEvent;
	private String attributes; //stores all attributes
	private ArrayList<String> labels;
	private ArrayList<String> subEvents;
	private ArrayList<String> owl;
	private ArrayList<String> description;
	private ArrayList<String> hasPlace;
	private ArrayList<Node> nodes;
	
	public ArrayList<String> hasPlace() {
		return hasPlace;
	}

	
	public void setHasPlace(ArrayList<String> hasPlace) {
		this.hasPlace = hasPlace;
	}

	public String getAttributes() {
		return attributes;
	}

	public void addNode (Node node)
	{
		if (nodes==null)
			nodes = new ArrayList<Node>();
		if (!nodes.contains(node))
			nodes.add(node);
	}
	
	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
	
	public void addElementLabel (String element)
	{
		labels.add(element);
		
	}
	
	public void addDescription (String element)
	{
		description.add(element);
		
	}
	public void addHasPlace (String element)
	{
		hasPlace.add(element);
		
	}
	public void addElementSubEvent (String element)
	{
		subEvents.add(element);
		
	}
	
	public void addElementOwl (String element)
	{
		owl.add(element);
		
	}
	
	public String getId() {
		return id;
	}

	public String getPreviousEvent() {
		return previousEvent;
	}

	public void setPreviousEvent(String previousEvent) {
		this.previousEvent = previousEvent;
	}

	public String getNextEvent() {
		return nextEvent;
	}

	public void setNextEvent(String nextEvent) {
		this.nextEvent = nextEvent;
	}

	public ArrayList<String> getLabels() {
		return labels;
	}

	public void setLabels(ArrayList<String> labels) {
		this.labels = labels;
	}

	public ArrayList<String> getSubEvents() {
		return subEvents;
	}

	public void setSubEvents(ArrayList<String> subEvents) {
		this.subEvents = subEvents;
	}

	public ArrayList<String> getOwl() {
		return owl;
	}

	public void setOwl(ArrayList<String> owl) {
		this.owl = owl;
	}

	public ArrayList<String> getDescription() {
		return description;
	}

	public void setDescription(ArrayList<String> description) {
		this.description = description;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RelationSnapshot() {
		this.type = "";
		this.subject = "";
		this.object = "";
		this.roleType = "";
		this.beginTimestamp = "";
		this.endTimestamp = "";
		this.attributes = "";
		this.id="";
		this.nextEvent="";
		this.previousEvent="";
		labels = new ArrayList<String>();
		description = new ArrayList<String>();
		subEvents = new ArrayList<String>();
		owl = new ArrayList<String>();
		hasPlace = new ArrayList<String>();
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getBeginTimestamp() {
		return beginTimestamp;
	}
	public void setBeginTimestamp(String beginTimestamp) {
		this.beginTimestamp = beginTimestamp;
	}
	public String getEndTimestamp() {
		return endTimestamp;
	}
	public void setEndTimestamp(String endTimestamp) {
		this.endTimestamp = endTimestamp;
	}
	
}
