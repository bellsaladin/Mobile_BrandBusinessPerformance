package com.bse.daizybuzz.model;

public class Produit {
	private int id;
	private String sku;
	private String libelle;
	private String categorieId;
	
	public Produit(int id, String sku,String libelle, String categorieId) {
		super();		
		this.id = id;
		this.sku = sku;
		this.libelle = libelle;
		this.categorieId = categorieId;
	}
	
	public Produit() {
	
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getSku() {
		return sku;
	}
	
	public void setSku(String sku) {
		this.sku = sku;
	}
	
	public String getLibelle() {
		return libelle;
	}
	
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	
	public String getCategorieId() {
		return categorieId;
	}
	
	public void setCategorieId(String categorieId) {
		this.categorieId = categorieId;
	}

	@Override
	public String toString() {
		return sku;
	}
	
	
}
