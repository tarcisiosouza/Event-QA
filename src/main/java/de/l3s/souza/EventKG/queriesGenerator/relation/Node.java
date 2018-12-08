package de.l3s.souza.EventKG.queriesGenerator.relation;

import de.l3s.souza.EventKG.queriesGenerator.nlp.Entity;

public class Node extends Entity {

	private String attributes;
	
	public Node(String type, String label, String varName) {
		super(type, label, varName);
		
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	
}
