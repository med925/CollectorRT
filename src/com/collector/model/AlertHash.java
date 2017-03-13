package com.collector.model;

public class AlertHash {

	private long idAlert;
	private String informationSymbol;
	private String operatorSymbol;
	private String informationValue;
	private String informationType;

	public AlertHash() {
		// TODO Auto-generated constructor stub
	}

	public AlertHash(long idAlert, String hashValue) {
		super();
		this.idAlert = idAlert;
	}

	public AlertHash(long idAlert, String informationSymbol, String operatorSymbol, String informationValue,
			String informationType) {
		super();
		this.idAlert = idAlert;
		this.informationSymbol = informationSymbol;
		this.operatorSymbol = operatorSymbol;
		this.informationValue = informationValue;
		this.informationType = informationType;
	}

	public long getIdAlert() {
		return idAlert;
	}

	public void setIdAlert(long idAlert) {
		this.idAlert = idAlert;
	}

	public String getInformationSymbol() {
		return informationSymbol;
	}

	public void setInformationSymbol(String informationSymbol) {
		this.informationSymbol = informationSymbol;
	}

	public String getOperatorSymbol() {
		return operatorSymbol;
	}

	public void setOperatorSymbol(String operatorSymbol) {
		this.operatorSymbol = operatorSymbol;
	}

	public String getInformationValue() {
		return informationValue;
	}

	public void setInformationValue(String informationValue) {
		this.informationValue = informationValue;
	}

	public String getInformationType() {
		return informationType;
	}

	public void setInformationType(String informationType) {
		this.informationType = informationType;
	}

	@Override
	public String toString() {
		return "idAlert = " + idAlert + " ------ " + informationSymbol + " " + operatorSymbol + " " + informationValue + " "
				+ informationType;
	}

}
