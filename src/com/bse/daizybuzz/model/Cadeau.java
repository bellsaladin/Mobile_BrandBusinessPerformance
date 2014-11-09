package com.bse.daizybuzz.model;

public class Cadeau {
	
	int id;
	String libelle;	
	
	public Cadeau() {
		super();		
	}
	
	public Cadeau(String libelle) {
		super();		
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
