package com.adobe.acrobatsign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserWidget {

	private String groupId;
	private String hidden;
	private String id;
	private String javascript;
	private String modifiedDate;
	private String name;
	private String status;
	private String url;
	private String isChecked;
	private String ownerEmail;

	public String getGroupId() {
		return groupId;
	}

	public String getHidden() {
		return hidden;
	}

	public String getId() {
		return id;
	}

	public String getIsChecked() {
		return isChecked;
	}

	public String getJavascript() {
		return javascript;
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

	public String getStatus() {
		return status;
	}

	public String getUrl() {
		return url;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setHidden(String hidden) {
		this.hidden = hidden;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIsChecked(String isChecked) {
		this.isChecked = isChecked;
	}

	public void setJavascript(String javascript) {
		this.javascript = javascript;
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

	public void setStatus(String status) {
		this.status = status;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
