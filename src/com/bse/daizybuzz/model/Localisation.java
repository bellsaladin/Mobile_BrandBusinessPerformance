package com.bse.daizybuzz.model;

public class Localisation {
	private int id;
	private String animateurId;
	private String imageFileName;
	private String superviseurId;
	private String pdvId;		
	private String longitude;
	private String latitude;	
	private String licenceRemplacee;
	private String motif;
	

	public Localisation() {
		
	}
	
	public Localisation(String animateurId, String imageFileName,  String superviseurId, String pdvId, String longitude, String latitude, String licenceRemplacee, String motif) {		
		this.animateurId = animateurId;
		this.imageFileName = imageFileName;
		this.superviseurId = superviseurId;
		this.pdvId = pdvId;
		this.longitude = longitude;
		this.latitude = latitude;
		this.licenceRemplacee = licenceRemplacee;
		this.motif = motif;
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

	public String getAnimateurId() {
		return animateurId;
	}

	public void setAnimateurId(String animateurId) {
		this.animateurId = animateurId;
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

	
}
