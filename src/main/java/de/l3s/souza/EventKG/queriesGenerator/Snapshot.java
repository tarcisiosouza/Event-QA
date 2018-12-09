package de.l3s.souza.EventKG.queriesGenerator;

import java.util.ArrayList;

public class Snapshot { //snapshot can be event, relation or entity
	
	private String type;
	private String subject;
	private String object;
	private String roleType;
	private String beginTimestamp;
	private String endTimestamp;
	private String previousEvent;
	private String nextEvent;
	private ArrayList<String> labels;
	private ArrayList<String> subEvents;
	private ArrayList<String> owl;
	private String description;
	
	
	public ArrayList<String> getOwl() {
		return owl;
	}

	public void setOwl(ArrayList<String> owl) {
		this.owl = owl;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Snapshot() {
		this.type = "";
		this.subject = "";
		this.object = "";
		this.roleType = "";
		this.beginTimestamp = "";
		this.endTimestamp = "";
		this.previousEvent= "";
		labels = new ArrayList<String>();
		subEvents = new ArrayList<String>();
		owl = new ArrayList<String>();
		
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
