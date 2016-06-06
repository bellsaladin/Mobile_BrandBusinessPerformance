package com.bse.daizybuzz.model;

public class Pdv {
	private int id;
	private String nom;
	private String ville;
	private String secteur;
	private String licence;
	
	public Pdv(int id, String nom, String licence, String ville, String secteur) {
		super();		
		this.id = id;
		this.nom = nom;
		this.licence = licence;
		this.ville = ville;
		this.secteur = secteur;
	}
	
	public Pdv() {
	
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
	public String getLicence() {
		return licence;
	}
	public void setLicence(String licence) {
		this.licence = licence;
	}
	public String getVille() {
		return ville;
	}
	public void setVille(String ville) {
		this.ville = ville;
	}
	public String getSecteur() {
		return secteur;
	}
	public void setSecteur(String secteur) {
		this.secteur = secteur;
	}
	
}
