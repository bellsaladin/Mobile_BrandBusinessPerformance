package com.bse.daizybuzz.model;

public class Produit {
	private int id;
	private String sku;
	private String libelle;
	private String categorieId;
	private String type; // REF, WM, CTV ... etc
	private int addedLocaly;
	
	public Produit(int id, String sku,String libelle, String categorieId) {
		super();
		this.id = id;
		this.sku = sku;
		this.libelle = libelle;
		this.categorieId = categorieId;
	}
	
	public Produit(int id, String sku,String libelle, String categorieId, int addedLocaly) {
		super();		
		this.id = id;
		this.sku = sku;
		this.libelle = libelle;
		this.categorieId = categorieId;
		this.setAddedLocaly(addedLocaly);
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

	public boolean isAddedLocaly() {
		return ((addedLocaly==1)?true:false);
	}
	
	public int getAddedLocaly() {
		return addedLocaly;
	}

	public void setAddedLocaly(int wasAddedLocaly) {
		this.addedLocaly = wasAddedLocaly;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
