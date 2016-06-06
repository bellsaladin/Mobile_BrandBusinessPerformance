package com.bse.daizybuzz.model;

public class Localisation {
	private int id;
	private String sfoId;
	private String imageFileName;
	private String superviseurId;
	private String pdvId;		
	private String longitude;
	private String latitude;	
	private String licenceRemplacee;
	private String motif;
	private String insertedInServerWithId;
	private String dateCreation;

	public Localisation() {
		
	}
	
	public Localisation(String sfoId, String imageFileName,  String superviseurId, String pdvId, String longitude, String latitude, String licenceRemplacee, String motif, String dateCreation) {		
		this.sfoId = sfoId;
		this.imageFileName = imageFileName;
		this.superviseurId = superviseurId;
		this.pdvId = pdvId;
		this.longitude = longitude;
		this.latitude = latitude;
		this.licenceRemplacee = licenceRemplacee;
		this.motif = motif;
		this.dateCreation = dateCreation;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCheminImage() {
		return imageFileName;
	}

	public void setCheminImage(String cheminImage) {
		this.imageFileName = cheminImage;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getPdvId() {
		return pdvId;
	}

	public void setPdvId(String pdvId) {
		this.pdvId = pdvId;
	}

	public String getSfoId() {
		return sfoId;
	}

	public void setSfoId(String sfoId) {
		this.sfoId = sfoId;
	}

	public String getLicenceRemplacee() {
		return licenceRemplacee;
	}

	public void setLicenceRemplacee(String licenceRemplacee) {
		this.licenceRemplacee = licenceRemplacee;
	}
	
	public String getSuperviseurId() {
		return superviseurId;
	}

	public void setSuperviseurId(String superviseurId) {
		this.superviseurId = superviseurId;
	}

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
	}
	
	public String getInsertedInServerWithId() {
		return insertedInServerWithId;
	}

	public void setInsertedInServerWithId(String insertedInServerWithId) {
		this.insertedInServerWithId = insertedInServerWithId;
	}

	public String getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}
	
}
