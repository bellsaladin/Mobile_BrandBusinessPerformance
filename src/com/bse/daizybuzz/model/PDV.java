package com.bse.daizybuzz.model;

public class PDV {
	private int id;
	private String nom;
	private int licence;
	
	public PDV(String nom, int licence) {
		super();		
		this.nom = nom;
		this.licence = licence;
	}
	
	public PDV() {
	
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
	public int getLicence() {
		return licence;
	}
	public void setLicence(int licence) {
		this.licence = licence;
	}
	
	
}
