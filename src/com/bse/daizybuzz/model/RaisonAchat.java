package com.bse.daizybuzz.model;

public class RaisonAchat {
	
	int id;
	String libelle;	
	
	
	public RaisonAchat(){
		
	}
	
	public RaisonAchat(int id, String libelle) {
		super();		
		this.id = id;
		this.libelle = libelle;		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String nom) {
		this.libelle = nom;
	}
	
}
