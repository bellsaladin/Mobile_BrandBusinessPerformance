package com.bse.daizybuzz.model;

public class Marque {
	
	int id;
	String libelle;	
	
	public Marque() {
		super();		
	}
	
	public Marque(String libelle) {
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
