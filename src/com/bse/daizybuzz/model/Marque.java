package com.bse.daizybuzz.model;

import java.util.ArrayList;
import java.util.List;

public class Marque {
	
	int id;
	String libelle;	
	List<Categorie> categories;
	
	public Marque() {
		categories = new ArrayList<Categorie>();
	}
	
	public Marque(int id, String libelle) {	
		this.id = id;
		this.libelle = libelle;
		categories = new ArrayList<Categorie>();
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

	public List<Categorie> getCategories() {
		return categories;
	}

	public void setCategories(List<Categorie> categories) {
		this.categories = categories;
	}
	
}
