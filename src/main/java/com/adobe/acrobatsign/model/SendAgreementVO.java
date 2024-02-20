package com.adobe.acrobatsign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendAgreementVO {

	private String name;

	private String message;

	private String approverEmail;

	private String signerEmail;

	public String getApproverEmail() {
		return approverEmail;
	}

	public String getMessage() {
		return message;
	}

	public String getName() {
		return name;
	}

	public String getSignerEmail() {
		return signerEmail;
	}

	public void setApproverEmail(String approverEmail) {
		this.approverEmail = approverEmail;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSignerEmail(String signerEmail) {
		this.signerEmail = signerEmail;
	}
}
