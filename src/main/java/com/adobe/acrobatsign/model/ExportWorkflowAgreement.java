package com.adobe.acrobatsign.model;

public class ExportWorkflowAgreement {
	private String agreementId;

	private String agreementName;
	private String workflowName;
	private String status;

	public String getAgreementId() {
		return agreementId;
	}

	public String getAgreementName() {
		return agreementName;
	}

	public String getStatus() {
		return status;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setAgreementId(String agreementId) {
		this.agreementId = agreementId;
	}

	public void setAgreementName(String agreementName) {
		this.agreementName = agreementName;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

}
