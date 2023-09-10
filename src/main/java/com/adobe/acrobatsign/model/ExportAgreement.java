package com.adobe.acrobatsign.model;

public class ExportAgreement {
	private String agreementId;

	private String agreementName;
	private String status;
	private String userEmail;
	private String modifiedDate;

	public String getAgreementId() {
		return agreementId;
	}

	public String getAgreementName() {
		return agreementName;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public String getStatus() {
		return status;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setAgreementId(String agreementId) {
		this.agreementId = agreementId;
	}

	public void setAgreementName(String agreementName) {
		this.agreementName = agreementName;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

}
