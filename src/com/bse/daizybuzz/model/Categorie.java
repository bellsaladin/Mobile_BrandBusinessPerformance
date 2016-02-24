package com.bse.daizybuzz.model;

public class Categorie {
	private int id;	
	private String nom;
	private String context;
	private String parentId;
	
	public Categorie(int id, String nom, String context, String parentId) {
		super();
		this.id = id;
		this.nom = nom;
		this.context = context;
		this.parentId = parentId;
	}

	public Categorie() {
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	
}
