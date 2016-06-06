package com.bse.daizybuzz.model;

public class Rapport {
	private int id;
	private String achete;
	private String trancheAgeId;
	private String sexe;
	private String raisonAchatId;
	private String raisonRefusId;
	private String fidelite;
	private String marqueHabituelleId;
	private String marqueHabituelleQte;
	private String marqueAcheteeId;
	private String marqueAcheteeQte;
	private String cadeauId;
	private String tombola;
	private String commentaire;	
	private String localisationId;	
	private String dateCreation;
	
	public Rapport() {
	
	}
	
	public Rapport( String achete, String trancheAgeId, String sexe,
			String fidelite, String raisonAchatId, 
			String marqueHabituelleId, String marqueHabituelleQte,
			String marqueAcheteeId, String marqueAcheteeQte, String cadeauId,
			String tombola, String localisationId, String dateCreation) {		
		this.achete = achete;
		this.trancheAgeId = trancheAgeId;
		this.sexe = sexe;
		this.raisonAchatId = raisonAchatId;		
		this.fidelite = fidelite;
		this.marqueHabituelleId = marqueHabituelleId;
		this.marqueHabituelleQte = marqueHabituelleQte;
		this.marqueAcheteeId = marqueAcheteeId;
		this.marqueAcheteeQte = marqueAcheteeQte;
		this.cadeauId = cadeauId;
		this.tombola = tombola;		
		this.localisationId = localisationId;
		this.setDateCreation(dateCreation);
	}
	
	

	public Rapport(String achete, String trancheAgeId, String sexe,
			String raisonRefusId, 
			String marqueHabituelleId, String marqueHabituelleQte,
			String commentaire, String localisationId,  String dateCreation) {		
		this.achete = achete;
		this.trancheAgeId = trancheAgeId;
		this.sexe = sexe;		
		this.raisonRefusId = raisonRefusId;
		this.marqueHabituelleId = marqueHabituelleId;
		this.marqueHabituelleQte = marqueHabituelleQte;
		this.commentaire = commentaire;
		this.localisationId = localisationId;
		this.setDateCreation(dateCreation);
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAchete() {
		return achete;
	}
	public void setAchete(String achete) {
		this.achete = achete;
	}
	public String getTrancheAgeId() {
		return trancheAgeId;
	}
	public void setTrancheAgeId(String trancheAgeId) {
		this.trancheAgeId = trancheAgeId;
	}
	public String getSexe() {
		return sexe;
	}
	public void setSexe(String sexe) {
		this.sexe = sexe;
	}
	public String getRaisonAchatId() {
		return raisonAchatId;
	}
	public void setRaisonAchatId(String raisonAchatId) {
		this.raisonAchatId = raisonAchatId;
	}
	public String getRaisonRefusId() {
		return raisonRefusId;
	}
	public void setRaisonRefusId(String raisonRefusId) {
		this.raisonRefusId = raisonRefusId;
	}
	public String getFidelite() {
		return fidelite;
	}
	public void setFidelite(String fidelite) {
		this.fidelite = fidelite;
	}
	public String getMarqueHabituelleId() {
		return marqueHabituelleId;
	}
	public void setMarqueHabituelleId(String marqueHabituelleId) {
		this.marqueHabituelleId = marqueHabituelleId;
	}
	public String getMarqueHabituelleQte() {
		return marqueHabituelleQte;
	}
	public void setMarqueHabituelleQte(String marqueHabituelleQte) {
		this.marqueHabituelleQte = marqueHabituelleQte;
	}
	public String getMarqueAcheteeId() {
		return marqueAcheteeId;
	}
	public void setMarqueAcheteeId(String marqueAcheteeId) {
		this.marqueAcheteeId = marqueAcheteeId;
	}
	public String getMarqueAcheteeQte() {
		return marqueAcheteeQte;
	}
	public void setMarqueAcheteeQte(String marqueAcheteeQte) {
		this.marqueAcheteeQte = marqueAcheteeQte;
	}
	public String getCadeauId() {
		return cadeauId;
	}
	public void setCadeauId(String cadeauId) {
		this.cadeauId = cadeauId;
	}
	public String getTombola() {
		return tombola;
	}
	public void setTombola(String tombola) {
		this.tombola = tombola;
	}
	
	public String getCommentaire() {
		return commentaire;
	}
	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
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
