package com.adobe.acrobatsign.model;

public class UserGroup {

	private String groupId;
    private String createdDate;
    private String groupName;
    private boolean isDefaultGroup;
 
    
    public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public boolean getIsDefaultGroup() {
		return isDefaultGroup;
	}
	public void setIsDefaultGroup(boolean isDefaultGroup) {
		this.isDefaultGroup = isDefaultGroup;
	}
}
