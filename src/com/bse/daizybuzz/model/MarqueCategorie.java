package com.bse.daizybuzz.model;

public class MarqueCategorie {
	private String marqueId;
	private String categorieId;

	public MarqueCategorie(String marqueId, String categorieId) {
		super();
		this.marqueId = marqueId;
		this.categorieId = categorieId;
	}
	
	public MarqueCategorie() {
		super();
	}
	
	public String getMarqueId() {
		return marqueId;
	}
	public void setMarqueId(String marqueId) {
		this.marqueId = marqueId;
	}
	public String getCategorieId() {
		return categorieId;
	}
	public void setCategorieId(String categorieId) {
		this.categorieId = categorieId;
	}
	
}
