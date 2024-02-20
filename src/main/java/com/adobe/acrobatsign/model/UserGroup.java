package com.adobe.acrobatsign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGroup {

	private String groupId;
	private String createdDate;
	private String groupName;
	private boolean isDefaultGroup;

	public String getCreatedDate() {
		return createdDate;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public boolean getIsDefaultGroup() {
		return isDefaultGroup;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setIsDefaultGroup(boolean isDefaultGroup) {
		this.isDefaultGroup = isDefaultGroup;
	}
}
