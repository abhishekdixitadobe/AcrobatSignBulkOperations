package com.adobe.acrobatsign.model;

import java.util.List;

public class AgreementInfo {

	private String groupId;
	private String message;
	private List<ParticipantSet> participantSet;
	private String senderEmail;
	private String name;
	private String status;
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<ParticipantSet> getParticipantSet() {
		return participantSet;
	}
	public void setParticipantSet(List<ParticipantSet> participantSet) {
		this.participantSet = participantSet;
	}
	public String getSenderEmail() {
		return senderEmail;
	}
	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
