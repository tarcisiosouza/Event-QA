package de.l3s.souza.EventKG.graphGenerator;

import java.util.ArrayList;

public class ElementVertex {
	
	private String id;
	private String roleType; //for relations
	private String cleanLabel; //always used whatever language available
	private String fullEnglishLabel;
	private String fullLabel; //used if English label is not provided
	private String type; //relation, entity or event
	private ArrayList<String> timestamps;
	private String beginTimeStamp;
	private String endTimeStamp;
	private ArrayList<String> description;
	private ArrayList<String> owl;
	private ArrayList<String> labels;
	
	
	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public ArrayList<String> getTimestamps() {
		return timestamps;
	}

	public void setTimestamps(ArrayList<String> timestamps) {
		this.timestamps = timestamps;
	}
	
	public String getFullLabel() {
		return fullLabel;
	}

	public void setFullLabel(String fullLabel) {
		this.fullLabel = fullLabel;
	}

	public String getCleanLabel() {
		return cleanLabel;
	}

	public String getFullEnglishLabel() {
		return fullEnglishLabel;
	}

	public void setFullEnglishLabel(String fullEnglishLabel) {
		this.fullEnglishLabel = fullEnglishLabel;
	}
	
	public void setCleanLabel(String cleanLabel) {
		this.cleanLabel = cleanLabel;
	}

	public String getId() {
		return id;
	}
	public ArrayList<String> getDescription() {
		return description;
	}
	public void setDescription(ArrayList<String> description) {
		this.description = description;
	}
	public void setOwl(ArrayList<String> owl) {
		this.owl = owl;
	}
	public void setLabels(ArrayList<String> labels) {
		this.labels = labels;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public ElementVertex() {
		
		owl = new ArrayList<String>();
	}
	
	public ArrayList<String> getOwl() {
		return owl;
	}
	
	public void setOwl(String value) {
		owl.add(value);
	}
	
	public ArrayList<String> getLabels() {
		return labels;
	}
	
	public void setLabels(String value) {
		labels.add(value);
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getBeginTimeStamp() {
		return beginTimeStamp;
	}
	
	public void setBeginTimeStamp(String beginTimeStamp) {
		this.beginTimeStamp = beginTimeStamp;
	}
	
	public String getEndTimeStamp() {
		return endTimeStamp;
	}
	
	public void setEndTimeStamp(String endTimeStamp) {
		this.endTimeStamp = endTimeStamp;
	}
	
	public String toString()
    {
        return id;
    }
	
	public int hashCode()
    {
        return toString().hashCode();
    }
	
	public boolean equals(Object o)
	{
	    return (o instanceof ElementVertex) && (toString().equals(o.toString()));
	}
	
}
