package com.bse.daizybuzz.model;

public class Questionnaire {
	
	public static final String TYPE_SHELFSHARE = "SHELFSHARE";
	public static final String TYPE_DISPONIBILITE = "DISPONIBILITE";
	
	private int id;
	private String type;
	private String quantitiesData;
	private String localisationId;	
	private String dateCreation;
	private int nbrLignesTraitees;
	private float tempsRemplissage;
	
	public Questionnaire() {
		nbrLignesTraitees = 0;
		tempsRemplissage = 0;
	}
	

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getQuantitiesData() {
		return quantitiesData;
	}

	public void setQuantitiesData(String quantitiesData) {
		this.quantitiesData = quantitiesData;
	}

	public String getLocalisationId() {
		return localisationId;
	}

	public void setLocalisationId(String localisationId) {
		this.localisationId = localisationId;
	}

	public String getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}

	public int getNbrLignesTraitees() {
		return nbrLignesTraitees;
	}

	public void setNbrLignesTraitees(int nbrLignesTraitees) {
		this.nbrLignesTraitees = nbrLignesTraitees;
	}

	public float getTempsRemplissage() {
		return tempsRemplissage;
	}

	public void setTempsRemplissage(float tempsRemplissage) {
		this.tempsRemplissage = tempsRemplissage;
	}
		
}
