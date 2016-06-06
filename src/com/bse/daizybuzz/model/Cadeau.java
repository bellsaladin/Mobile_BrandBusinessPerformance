package com.bse.daizybuzz.model;

public class Cadeau {
	
	int id;
	String libelle;	
	
	
	public Cadeau(){
		
	}
	
	public Cadeau(int id, String libelle) {
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
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	
	@Override
	public String toString(){
		return libelle;
	}
}
