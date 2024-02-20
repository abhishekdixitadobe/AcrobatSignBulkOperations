package com.adobe.acrobatsign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReminderParticipants {

	private String participantId;
	private String name;
	private String email;

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getParticipantId() {
		return participantId;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParticipantId(String participantId) {
		this.participantId = participantId;
	}

}
