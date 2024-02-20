package com.adobe.acrobatsign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SendVO {
	private String name;

	private String message;

	private List<ParticipantSet> participantSetsInfo;

	private String signatureType = "ESIGN";

	private String state;

	public String getMessage() {
		return message;
	}

	public String getName() {
		return name;
	}

	public List<ParticipantSet> getParticipantSetsInfo() {
		return participantSetsInfo;
	}

	public String getSignatureType() {
		return signatureType;
	}

	public String getState() {
		return state;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParticipantSetsInfo(List<ParticipantSet> participantSetsInfo) {
		this.participantSetsInfo = participantSetsInfo;
	}

	public void setSignatureType(String signatureType) {
		this.signatureType = signatureType;
	}

	public void setState(String state) {
		this.state = state;
	}
}
