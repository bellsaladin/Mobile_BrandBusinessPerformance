package com.bse.daizybuzz.model;

public class PdvPoi {
	private String pdvId;
	private String poiId;

	public PdvPoi(String pdvId, String poiId) {
		super();
		this.pdvId = pdvId;
		this.poiId = poiId;
	}
	
	public PdvPoi() {
		super();
	}
	
	public String getPdvId() {
		return pdvId;
	}
	public void setPdvId(String pdvId) {
		this.pdvId = pdvId;
	}
	public String getPoiId() {
		return poiId;
	}
	public void setPoiId(String poiId) {
		this.poiId = poiId;
	}
	
}
