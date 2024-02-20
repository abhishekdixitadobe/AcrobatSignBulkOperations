package com.adobe.acrobatsign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
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
	private String isChecked;

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getId() {
		return id;
	}

	public String getIsChecked() {
		return isChecked;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public String getName() {
		return name;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public String getSharingMode() {
		return sharingMode;
	}

	public String getStatus() {
		return status;
	}

	public String[] getTemplateTypes() {
		return templateTypes;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIsChecked(String isChecked) {
		this.isChecked = isChecked;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public void setSharingMode(String sharingMode) {
		this.sharingMode = sharingMode;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTemplateTypes(String[] templateTypes) {
		this.templateTypes = templateTypes;
	}

}
