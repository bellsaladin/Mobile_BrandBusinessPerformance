package com.bse.daizybuzz.model;

public class QuestionnaireShelfShare {
	private int id;
	private String quantitiesData;
	private String localisationId;	
	private String dateCreation;
	
	public QuestionnaireShelfShare() {
	
	}
	
	public QuestionnaireShelfShare( String quantitiesData, String localisationId, String dateCreation) {			
		this.localisationId = localisationId;
		this.setDateCreation(dateCreation);
	}
	

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	
}
