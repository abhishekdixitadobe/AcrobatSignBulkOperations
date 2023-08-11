package com.adobe.acrobatsign.model;

import java.util.List;

public class UserEvent {

    
	private String id;
	
	private List<ReminderParticipants> reminderParticipants;
	
	
	public List<ReminderParticipants> getReminderParticipants() {
		return reminderParticipants;
	}
	public void setReminderParticipants(List<ReminderParticipants> reminderParticipants) {
		this.reminderParticipants = reminderParticipants;
	}
	
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParticipantId() {
		return participantId;
	}
	public void setParticipantId(String participantId) {
		this.participantId = participantId;
	}
	public String getVersionId() {
		return versionId;
	}
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getActingUserEmail() {
		return actingUserEmail;
	}
	public void setActingUserEmail(String actingUserEmail) {
		this.actingUserEmail = actingUserEmail;
	}
	public String getActingUserName() {
		return actingUserName;
	}
	public void setActingUserName(String actingUserName) {
		this.actingUserName = actingUserName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getParticipantEmail() {
		return participantEmail;
	}
	public void setParticipantEmail(String participantEmail) {
		this.participantEmail = participantEmail;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getParticipantRole() {
		return participantRole;
	}
	public void setParticipantRole(String participantRole) {
		this.participantRole = participantRole;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
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
		
}
