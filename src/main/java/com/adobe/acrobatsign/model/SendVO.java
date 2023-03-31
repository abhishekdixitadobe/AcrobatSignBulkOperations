package com.adobe.acrobatsign.model;

import java.util.List;

public class SendVO {
	private String name;
	
	private String message;
	
	private List<ParticipantSet> participantSetsInfo;
	
	private String signatureType = "ESIGN";
	
	private String state;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<ParticipantSet> getParticipantSetsInfo() {
		return participantSetsInfo;
	}

	public void setParticipantSetsInfo(List<ParticipantSet> participantSetsInfo) {
		this.participantSetsInfo = participantSetsInfo;
	}

	public String getSignatureType() {
		return signatureType;
	}

	public void setSignatureType(String signatureType) {
		this.signatureType = signatureType;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
