package de.l3s.souza.EventKG.queriesGenerator.nlp;

public class Entity {

	private String type; //person, location, organization
	private String label;
	private String varName;
	private String role; //if this entity is an object of a relation, then it has a role with the subject, otherwise is empty
	
	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	private boolean isVar;
	private boolean isEvent;
	protected static boolean isCountVar = false;
	private boolean isObject = false;
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public boolean isObject() {
		return isObject;
	}

	public void setObject(boolean isObject) {
		this.isObject = isObject;
	}

	public void setEvent(boolean isEvent) {
		this.isEvent = isEvent;
	}

	public boolean isCountVar() {
		return isCountVar;
	}

	public void setCountVar(boolean isCountVar) {
		this.isCountVar = isCountVar;
	}

	public Entity(String type, String label, String varName, boolean isVar) {
		
		this.type = type;
		this.label = label;
		this.varName = varName;
		this.isVar = isVar;
		
		if (varName.contains("event"))
			isEvent = true;
		else
			isEvent = false;
	}
	
public Entity(String type, String label, String varName) {
		
		this.type = type;
		this.label = label;
		this.varName = varName;
		
		if (varName.contains("event"))
			isEvent = true;
		else
			isEvent = false;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
		
	}
	
	public boolean isEvent ()
	{
		return isEvent;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public boolean isVar() {
		return isVar;
	}
	
	public void setVar(boolean isVar) {
		this.isVar = isVar;
	}
	
}
