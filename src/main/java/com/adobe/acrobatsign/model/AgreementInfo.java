package com.adobe.acrobatsign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementInfo {

	private String groupId;
	private String agreementId;
	private String message;
	private List<ParticipantSet> participantSet;
	private String senderEmail;
	private String name;
	private String status;
	private String modifiedDate;

	private String isChecked;

	public String getAgreementId() {
		return this.agreementId;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public String getIsChecked() {
		return this.isChecked;
	}

	public String getMessage() {
		return this.message;
	}

	public String getModifiedDate() {
		return this.modifiedDate;
	}

	public String getName() {
		return this.name;
	}

	public List<ParticipantSet> getParticipantSet() {
		return this.participantSet;
	}

	public String getSenderEmail() {
		return this.senderEmail;
	}

	public String getStatus() {
		return this.status;
	}

	public void setAgreementId(String agreementId) {
		this.agreementId = agreementId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setIsChecked(String isChecked) {
		this.isChecked = isChecked;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParticipantSet(List<ParticipantSet> participantSet) {
		this.participantSet = participantSet;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
