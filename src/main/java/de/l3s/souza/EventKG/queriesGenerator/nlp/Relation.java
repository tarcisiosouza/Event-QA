package de.l3s.souza.EventKG.queriesGenerator.nlp;

public class Relation {
	
	private String roleType;
	private Entity object;
	private Entity subject;
	private String id;
	
	public Relation(String roleType, Entity object, Entity subject,String id) {
		
		this.roleType = roleType;
		this.object = object;
		this.subject = subject;
		this.id = id;
	}
	
	public Relation ()
	{
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public Entity getObject() {
		return object;
	}
	public void setObject(Entity object) {
		this.object = object;
	}
	public Entity getSubject() {
		return subject;
	}
	public void setSubject(Entity subject) {
		this.subject = subject;
	}

}
