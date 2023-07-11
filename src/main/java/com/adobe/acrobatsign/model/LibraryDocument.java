package com.adobe.acrobatsign.model;

public class LibraryDocument {
	private boolean hidden;
	private String id;
	private String modifiedDate;
	private String name;
	private String sharingMode;
	private String[] templateTypes;
	private String creatorEmail;
	private String groupId;
	private String ownerEmail;
	private String status;
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSharingMode() {
		return sharingMode;
	}
	public void setSharingMode(String sharingMode) {
		this.sharingMode = sharingMode;
	}
	public String[] getTemplateTypes() {
		return templateTypes;
	}
	public void setTemplateTypes(String[] templateTypes) {
		this.templateTypes = templateTypes;
	}
	public String getCreatorEmail() {
		return creatorEmail;
	}
	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getOwnerEmail() {
		return ownerEmail;
	}
	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
