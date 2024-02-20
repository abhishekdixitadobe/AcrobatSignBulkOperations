package com.adobe.acrobatsign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEvent {

	private String id;

	private List<ReminderParticipants> reminderParticipants;

	private String participantId;
	private String versionId;

	private String type;
	private String actingUserEmail;
	private String actingUserName;
	private String date;
	private String description;
	private String participantEmail;
	private String comment;
	private String participantRole;
	private String device;

	public String getActingUserEmail() {
		return actingUserEmail;
	}

	public String getActingUserName() {
		return actingUserName;
	}

	public String getComment() {
		return comment;
	}

	public String getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	public String getDevice() {
		return device;
	}

	public String getId() {
		return id;
	}

	public String getParticipantEmail() {
		return participantEmail;
	}

	public String getParticipantId() {
		return participantId;
	}

	public String getParticipantRole() {
		return participantRole;
	}

	public List<ReminderParticipants> getReminderParticipants() {
		return reminderParticipants;
	}

	public String getType() {
		return type;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setActingUserEmail(String actingUserEmail) {
		this.actingUserEmail = actingUserEmail;
	}

	public void setActingUserName(String actingUserName) {
		this.actingUserName = actingUserName;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setParticipantEmail(String participantEmail) {
		this.participantEmail = participantEmail;
	}

	public void setParticipantId(String participantId) {
		this.participantId = participantId;
	}

	public void setParticipantRole(String participantRole) {
		this.participantRole = participantRole;
	}

	public void setReminderParticipants(List<ReminderParticipants> reminderParticipants) {
		this.reminderParticipants = reminderParticipants;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

}
